package com.kollider.engine.ecs.physics

import com.kollider.engine.ecs.Entity
import com.kollider.engine.ecs.System
import com.kollider.engine.ecs.rendering.Drawable

class CollisionSystem(private val worldBounds: Drawable.Rect) : System() {

    // Simple uniform grid broad-phase (replace with quadtree if you like)
    private val grid = UniformGrid(cellSize = 64f)

    override fun update(entities: List<Entity>, deltaTime: Float) {
        val collidables = entities.filter { it.get(Collider::class) != null}

        // clear previous-frame collision events
        collidables.forEach { it.get(Collider::class)!!.collisions.clear() }

        // rebuild spatial index
        grid.clear()
        collidables.forEach { e ->
            val p = e.get(Position::class)!!
            val c = e.get(Collider::class)!!
            grid.insert(e, p.x, p.y, c.width, c.height)
        }

        // pair tests from grid
        grid.forEachPotentialPairs { a, b ->
            val pa = a.get(Position::class)!!
            val pb = b.get(Position::class)!!
            val ca = a.get(Collider::class)!!
            val cb = b.get(Collider::class)!!

            if (aabbIntersects(pa.x, pa.y, ca.width, ca.height, pb.x, pb.y, cb.width, cb.height)) {
                ca.collisions.add(CollisionEvent(CollisionType.ENTITY, b, a))
                cb.collisions.add(CollisionEvent(CollisionType.ENTITY, a, b))
                // Optional: resolve penetration + velocity response here or in a separate ResolutionSystem
            }
        }

        // boundaries once per entity
        collidables.forEach { e ->
            val p = e.get(Position::class)!!
            val c = e.get(Collider::class)!!
            val right = p.x + c.width
            val bottom = p.y + c.height

            if (p.x < worldBounds.left)  c.collisions.add(CollisionEvent(CollisionType.BOUNDARY_LEFT))
            if (right > worldBounds.right) c.collisions.add(CollisionEvent(CollisionType.BOUNDARY_RIGHT))
            if (p.y < worldBounds.top)   c.collisions.add(CollisionEvent(CollisionType.BOUNDARY_TOP))
            if (bottom > worldBounds.bottom) c.collisions.add(CollisionEvent(CollisionType.BOUNDARY_BOTTOM))
        }
    }
}