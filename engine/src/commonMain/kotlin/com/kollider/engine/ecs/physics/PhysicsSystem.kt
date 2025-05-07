package com.kollider.engine.ecs.physics

import com.kollider.engine.ecs.Entity
import com.kollider.engine.ecs.System

/**
 * Updates entities by applying their velocity to their position.
 */
class PhysicsSystem(private val screenWidth: Int, private val screenHeight: Int) : System() {
    override fun update(entities: List<Entity>, deltaTime: Float) {
        entities.forEach { entity ->
            val position = entity.get(Position::class)
            val velocity = entity.get(Velocity::class)
            if (position != null && velocity != null) {
                position.x += velocity.vx * deltaTime
                position.y += velocity.vy * deltaTime
            }

            // Get the entities that have a collider and position component
            val collidableEntities = entities.filter {
                it.get(Collider::class) != null && it.get(Position::class) != null
            }

            // Check for collisions
            for (i in collidableEntities.indices) {

                for (j in i + 1 until collidableEntities.size) {
                    val entity1 = collidableEntities[i]
                    val entity2 = collidableEntities[j]

                    val collider1 = entity1.get(Collider::class)
                    val collider2 = entity2.get(Collider::class)

                    val position1 = entity1.get(Position::class)
                    val position2 = entity2.get(Position::class)

                    val bounds1 = BoundingBox(position1!!.x, position1.y, collider1!!.width, collider1.height)
                    val bounds2 = BoundingBox(position2!!.x, position2.y, collider2!!.width, collider2.height)

                    if (bounds1.intersects(bounds2)) {
                        // Handle collision
                        collider1.collisions.add(CollisionEvent(CollisionType.ENTITY, entity2))
                    }

                    // Check for boundary collisions
                    if (position1.x < 0) {
                        collider1.collisions.add(CollisionEvent(CollisionType.BOUNDARY_LEFT))
                    } else if (position1.x + collider1.width > screenWidth) {
                        collider1.collisions.add(CollisionEvent(CollisionType.BOUNDARY_RIGHT))
                    }

                    if (position1.y < 0) {
                        collider1.collisions.add(CollisionEvent(CollisionType.BOUNDARY_TOP))
                    } else if (position1.y + collider1.height > screenHeight) {
                        collider1.collisions.add(CollisionEvent(CollisionType.BOUNDARY_BOTTOM))
                    }

                    if (position2.x < 0) {
                        collider2.collisions.add(CollisionEvent(CollisionType.BOUNDARY_LEFT))
                    } else if (position2.x + collider2.width > screenWidth) {
                        collider2.collisions.add(CollisionEvent(CollisionType.BOUNDARY_RIGHT))
                    }

                    if (position2.y < 0) {
                        collider2.collisions.add(CollisionEvent(CollisionType.BOUNDARY_TOP))
                    } else if (position2.y + collider2.height > screenHeight) {
                        collider2.collisions.add(CollisionEvent(CollisionType.BOUNDARY_BOTTOM))
                    }
                }
            }
        }
    }
}
