package com.kollider.engine.ecs.physics

/**
 * Mutable axis-aligned bounding box helper used by collision detection utilities.
 *
 * ```kotlin
 * val box = BoundingBox(x = 0f, y = 0f, width = 16f, height = 16f)
 * if (box.intersects(otherBox)) {
 *     resolveCollision()
 * }
 * ```
 */
data class BoundingBox(
    var x: Float,
    var y: Float,
    var width: Float,
    var height: Float
) {
    /**
     * Checks if this bounding box intersects with another.
     */
    fun intersects(other: BoundingBox): Boolean =
        x < other.x + other.width &&
        x + width > other.x &&
        y < other.y + other.height &&
        y + height > other.y
}
