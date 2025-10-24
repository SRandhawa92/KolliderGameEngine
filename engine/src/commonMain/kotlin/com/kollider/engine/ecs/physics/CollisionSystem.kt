package com.kollider.engine.ecs.physics

import com.kollider.engine.core.WorldBounds
import com.kollider.engine.ecs.Entity
import com.kollider.engine.ecs.EntityView
import com.kollider.engine.ecs.System
import com.kollider.engine.ecs.World
import com.kollider.engine.ecs.rendering.Drawable

/**
 * Performs broad-phase collision detection using a uniform grid and populates [Collider.collisions].
 *
 * ```kotlin
 * world.addSystem(CollisionSystem(config.worldBounds))
 * ```
 */
class CollisionSystem(private val worldBounds: WorldBounds) : System() {

    @Deprecated(
        message = "Use CollisionSystem(WorldBounds) instead",
        replaceWith = ReplaceWith(
            "CollisionSystem(WorldBounds(width = bounds.width, height = bounds.height, originX = bounds.offsetX, originY = bounds.offsetY))"
        )
    )
    constructor(bounds: Drawable.Rect) : this(
        WorldBounds(
            width = bounds.width,
            height = bounds.height,
            originX = bounds.offsetX,
            originY = bounds.offsetY,
        )
    )

    // Simple uniform grid broad-phase (replace with quadtree if you like)
    private val grid = UniformGrid(cellSize = 64f)
    private lateinit var collidableView: EntityView

    /**
     * Prepares an [EntityView] for entities that carry both [Position] and [Collider].
     */
    override fun onAttach(world: World) {
        collidableView = world.view(Position::class, Collider::class)
    }

    /**
     * Clears previous collision events, rebuilds the spatial index, and records new collisions.
     *
     * ```kotlin
     * collisionSystem.update(entities, deltaTime)
     * ```
     */
    override fun update(entities: List<Entity>, deltaTime: Float) {
        val collidables = collidableView

        // clear previous-frame collision events
        collidables.forEach { entity ->
            entity.get(Collider::class)?.collisions?.clear()
        }

        // rebuild spatial index
        grid.clear()
        collidables.forEach { entity ->
            val position = entity.get(Position::class) ?: return@forEach
            val collider = entity.get(Collider::class) ?: return@forEach
            grid.insert(entity, position.x, position.y, collider.width, collider.height)
        }

        // pair tests from grid
        grid.forEachPotentialPairs { a, b ->
            val pa = a.get(Position::class) ?: return@forEachPotentialPairs
            val pb = b.get(Position::class) ?: return@forEachPotentialPairs
            val ca = a.get(Collider::class) ?: return@forEachPotentialPairs
            val cb = b.get(Collider::class) ?: return@forEachPotentialPairs

            if (aabbIntersects(pa.x, pa.y, ca.width, ca.height, pb.x, pb.y, cb.width, cb.height)) {
                ca.collisions.add(CollisionEvent(CollisionType.ENTITY, b, a))
                cb.collisions.add(CollisionEvent(CollisionType.ENTITY, a, b))
                // Optional: resolve penetration + velocity response here or in a separate ResolutionSystem
            }
        }

        // boundaries once per entity
        collidables.forEach { e ->
            val p = e.get(Position::class) ?: return@forEach
            val c = e.get(Collider::class) ?: return@forEach
            val left = p.x
            val top = p.y
            val right = left + c.width
            val bottom = top + c.height

            if (left < worldBounds.left)  c.collisions.add(CollisionEvent(CollisionType.BOUNDARY_LEFT))
            if (right > worldBounds.right) c.collisions.add(CollisionEvent(CollisionType.BOUNDARY_RIGHT))
            if (top < worldBounds.top)   c.collisions.add(CollisionEvent(CollisionType.BOUNDARY_TOP))
            if (bottom > worldBounds.bottom) c.collisions.add(CollisionEvent(CollisionType.BOUNDARY_BOTTOM))
        }
    }
}
