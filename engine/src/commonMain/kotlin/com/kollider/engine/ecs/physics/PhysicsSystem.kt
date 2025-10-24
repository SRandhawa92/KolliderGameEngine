package com.kollider.engine.ecs.physics

import com.kollider.engine.ecs.Entity
import com.kollider.engine.ecs.EntityView
import com.kollider.engine.ecs.System
import com.kollider.engine.ecs.World

/**
 * Updates entities by applying their velocity to their position.
 */
class PhysicsSystem() : System() {
    private lateinit var dynamicView: EntityView

    override fun onAttach(world: World) {
        dynamicView = world.view(Position::class, Velocity::class)
    }

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
