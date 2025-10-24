package com.kollider.engine.ecs.physics

import com.kollider.engine.ecs.Entity
import com.kollider.engine.ecs.EntityView
import com.kollider.engine.ecs.System
import com.kollider.engine.ecs.World

/**
 * Basic integration system that applies [Velocity] to [Position] each frame.
 *
 * ```kotlin
 * world.addSystem(PhysicsSystem())
 * ```
 */
class PhysicsSystem() : System() {
    private lateinit var dynamicView: EntityView

    /**
     * Caches an entity view for `Position + Velocity` pairs.
     */
    override fun onAttach(world: World) {
        dynamicView = world.view(Position::class, Velocity::class)
    }

    /**
     * Performs semi-implicit Euler integration on all tracked entities.
     *
     * ```kotlin
     * // Called automatically by the world each frame
     * physicsSystem.update(entities, deltaTime)
     * ```
     */
    override fun update(entities: List<Entity>, deltaTime: Float) {
        dynamicView.forEach { entity ->
            val pos = entity.get(Position::class) ?: return@forEach
            val vel = entity.get(Velocity::class) ?: return@forEach
            // semi-implicit Euler
            pos.x += vel.vx * deltaTime
            pos.y += vel.vy * deltaTime
        }
    }
}
