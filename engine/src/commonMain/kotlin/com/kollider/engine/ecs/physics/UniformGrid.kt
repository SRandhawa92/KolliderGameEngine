package com.kollider.engine.ecs.physics

import com.kollider.engine.ecs.Entity
import kotlin.math.floor

/**
 * Checks whether two axis-aligned bounding boxes intersect.
 */
@PublishedApi
internal fun aabbIntersects(
    ax: Float, ay: Float, aw: Float, ah: Float,
    bx: Float, by: Float, bw: Float, bh: Float
): Boolean {
    return ax < bx + bw && ax + aw > bx && ay < by + bh && ay + ah > by
}

/**
 * Extremely simple uniform grid broad phase used to prune collision checks.
 *
 * Typical usage records all colliders every frame, then iterates potential pairs:
 *
 * ```kotlin
 * grid.clear()
 * colliders.forEach { (entity, bounds) ->
 *     grid.insert(entity, bounds.x, bounds.y, bounds.width, bounds.height)
 * }
 * grid.forEachPotentialPairs { a, b -> narrowPhaseTest(a, b) }
 * ```
 */
class UniformGrid(
    val cellSize: Float = 64f,
    maxBuckets: Int = 4096 // soft cap; tune per game
) {
    // Hash -> reusable bucket list
    val buckets = HashMap<Int, MutableList<Entity>>(maxBuckets)
    val pairBuffer = ArrayList<Pair<Entity, Entity>>(256)

    /**
     * Empties all buckets and cached pairs without reallocating.
     */
    fun clear() {
        // avoid re-allocating bucket lists; just clear
        buckets.values.forEach { it.clear() }
        pairBuffer.clear()
    }

    /**
     * Inserts an entity and its bounding box into every grid cell it overlaps.
     *
     * ```kotlin
     * grid.insert(entity, x, y, width, height)
     * ```
     */
    fun insert(e: Entity, x: Float, y: Float, w: Float, h: Float) {
        val minCx = floor(x / cellSize).toInt()
        val minCy = floor(y / cellSize).toInt()
        val maxCx = floor((x + w) / cellSize).toInt()
        val maxCy = floor((y + h) / cellSize).toInt()

        for (cy in minCy..maxCy) {
            for (cx in minCx..maxCx) {
                val key = hash(cx, cy)
                val list = buckets.getOrPut(key) { mutableListOf() }
                list.add(e)
            }
        }
    }

    /**
     * Calls [consume] for each *unique* potential pair detected in populated cells.
     *
     * ```kotlin
     * grid.forEachPotentialPairs { a, b ->
     *     if (aabbIntersects(a, b)) resolveCollision(a, b)
     * }
     * ```
     */
    inline fun forEachPotentialPairs(crossinline consume: (Entity, Entity) -> Unit) {
        pairBuffer.clear()

        // For each bucket, emit unique pairs within the bucket
        for (bucket in buckets.values) {
            val n = bucket.size
            for (i in 0 until n) {
                val a = bucket[i]
                for (j in i + 1 until n) {
                    val b = bucket[j]
                    pairBuffer.add(Pair(a, b))
                }
            }
        }

        // Emit to consumer
        val n = pairBuffer.size
        for (i in 0 until n) {
            val (a, b) = pairBuffer[i]
            consume(a, b)
        }
    }

    /**
     * Combines 2D cell coordinates into a pseudo-unique hash key.
     */
    private fun hash(cx: Int, cy: Int): Int {
        // A simple 2D hash (32-bit); large primes to reduce collisions
        return (cx * 73856093) xor (cy * 19349663)
    }
}
