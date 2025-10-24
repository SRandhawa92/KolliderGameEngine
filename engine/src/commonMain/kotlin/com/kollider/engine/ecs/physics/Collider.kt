package com.kollider.engine.ecs.physics

import com.kollider.engine.ecs.Component
import com.kollider.engine.ecs.Entity

/**
 * Enumerates the types of collisions recognised by [CollisionSystem].
 */
enum class CollisionType {
    BOUNDARY_TOP,
    BOUNDARY_BOTTOM,
    BOUNDARY_LEFT,
    BOUNDARY_RIGHT,
    ENTITY
}

/**
 * Describes a collision detected during the last physics update.
 *
 * ```kotlin
 * collider.collisions.forEach { event -> handle(event) }
 * ```
 */
data class CollisionEvent(
    val type: CollisionType,
    val entity: Entity? = null, // null for boundary collisions
    val other: Entity? = null // the other entity involved in the collision, if applicable
)

/**
 * Axis-aligned collision bounds attached to an entity.
 *
 * Collisions detected by [CollisionSystem] populate the [collisions] list each frame.
 *
 * ```kotlin
 * val collider = Collider(width = 32f, height = 32f)
 * entity.add(collider)
 * ```
 */
data class Collider(
    val width: Float,
    val height: Float,
    val collisions: MutableList<CollisionEvent> = mutableListOf()
) : Component()
