package com.kollider.flappybird.systems

import com.kollider.engine.core.GameConfig
import com.kollider.engine.ecs.Entity
import com.kollider.engine.ecs.System
import com.kollider.engine.ecs.World
import com.kollider.engine.ecs.input.InputComponent
import com.kollider.engine.ecs.physics.Collider
import com.kollider.engine.ecs.physics.CollisionType
import com.kollider.engine.ecs.physics.Position
import com.kollider.engine.ecs.physics.Velocity
import com.kollider.flappybird.components.BirdComponent
import com.kollider.flappybird.components.ObstacleComponent

fun World.birdSystem(jumpSpeed: Float, gravity: Float, config: GameConfig) {
    addSystem(BirdSystem(jumpSpeed, gravity, config, this))
}

class BirdSystem(
    private val jumpSpeed: Float,
    private val gravity: Float,
    private val config: GameConfig,
    private val world: World
): System() {
    override fun update(entities: List<Entity>, deltaTime: Float) {
        var hitObstacle = false

        entities.filter { it.has(BirdComponent::class) }.forEach { bird ->
            bird.get(Position::class)!!
            val velocity = bird.get(Velocity::class)!!
            val input = bird.get(InputComponent::class)!!
            val collider = bird.get(Collider::class)!!

            // Apply jump
            if (input.shoot) {
                velocity.vy = jumpSpeed
            } else {
                velocity.vy += gravity * deltaTime
            }

            // Handle collisions
            val iterator = collider.collisions.iterator()
            while (iterator.hasNext()) {
                val collision = iterator.next()
                when (collision.type) {
                    CollisionType.ENTITY -> {
                        hitObstacle = true
                        iterator.remove()
                    }
                    else -> {}
                }
            }
        }

        if (hitObstacle) {
            entities.filter { it.has(ObstacleComponent::class) }.forEach { bird ->
                world.removeEntity(bird)
            }
            hitObstacle = false
        }
    }
}