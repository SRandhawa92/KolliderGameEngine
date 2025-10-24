package com.kollider.engine.ecs.physics

import com.kollider.engine.ecs.Entity
import com.kollider.engine.ecs.System

/**
 * Updates entities by applying their velocity to their position.
 */
class PhysicsSystem() : System() {
    override fun update(entities: List<Entity>, deltaTime: Float) {
        // Prefer iterating a view prepared by the world, not filtering here.
        for (e in entities) {
            val pos = e.get(Position::class) ?: continue
            val vel = e.get(Velocity::class) ?: continue
            // semi-implicit Euler
            pos.x += vel.vx * deltaTime
            pos.y += vel.vy * deltaTime
        }
    }
}
