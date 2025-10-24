package com.kollider.engine.ecs.physics

import com.kollider.engine.core.WorldBounds
import com.kollider.engine.ecs.Entity
import com.kollider.engine.ecs.System
import com.kollider.engine.ecs.rendering.Drawable

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

    override fun update(entities: List<Entity>, deltaTime: Float) {
        val collidables = entities.filter { it.get(Collider::class) != null}

        // clear previous-frame collision events
        collidables.forEach { it.get(Collider::class)!!.collisions.clear() }

        // rebuild spatial index
        grid.clear()
        collidables.forEach { e ->
            val p = e.get(Position::class) ?: return@forEach
            val c = e.get(Collider::class) ?: return@forEach
            grid.insert(e, p.x, p.y, c.width, c.height)
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
