package com.kollider.engine.ecs.physics

import com.kollider.engine.ecs.Component
import com.kollider.engine.ecs.Entity

/**
 * The type of collision that occurred.
 */
enum class CollisionType {
    BOUNDARY_TOP,
    BOUNDARY_BOTTOM,
    BOUNDARY_LEFT,
    BOUNDARY_RIGHT,
    ENTITY
}

/**
 * A collision event that occurred.
 */
data class CollisionEvent(
    val type: CollisionType,
    val entity: Entity? = null // null for boundary collisions
)

/**
 * A component representing an entity's collision bounds.
 * The collider is assumed to be axis-aligned and centered at the entity's position.
 */
data class Collider(
    val width: Float,
    val height: Float,
    val collisions: MutableList<CollisionEvent> = mutableListOf()
) : Component()
