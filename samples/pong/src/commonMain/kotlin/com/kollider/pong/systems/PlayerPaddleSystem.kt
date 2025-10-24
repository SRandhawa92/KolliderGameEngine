package com.kollider.pong.systems

import com.kollider.engine.ecs.Entity
import com.kollider.engine.ecs.System
import com.kollider.engine.ecs.World
import com.kollider.engine.ecs.EntityView
import com.kollider.engine.ecs.input.InputComponent
import com.kollider.engine.ecs.physics.Collider
import com.kollider.engine.ecs.physics.CollisionType
import com.kollider.engine.ecs.physics.Velocity
import com.kollider.engine.ecs.require
import com.kollider.pong.components.PlayerPaddleComponent

fun World.playerPaddleSystem(paddleSpeed: Float) {
    addSystem(PlayerPaddleSystem(paddleSpeed))
}

/**
 * System that updates the player-controlled paddle's velocity based on input.
 * Assumes that the InputSystem updates the InputComponent properties (e.g. moveUp/moveDown).
 */
class PlayerPaddleSystem(
    private val paddleSpeed: Float
) : System() {
    private lateinit var playerView: EntityView

    override fun onAttach(world: World) {
        playerView = world.view(PlayerPaddleComponent::class, InputComponent::class)
    }

    override fun update(entities: List<Entity>, deltaTime: Float) {
        // Process each entity that has an InputComponent and a PlayerPaddleComponent.
        playerView.forEach { entity ->
                val input = entity.require<InputComponent>()
                val velocity = entity.require<Velocity>()
                val collider = entity.require<Collider>()

                // Handle paddle movement based on input.
                velocity.vx = when {
                    input.movement.x < 0 -> -paddleSpeed
                    input.movement.x > 0 -> paddleSpeed
                    else -> 0f
                }

                // Handle collisions.
                val iterator = collider.collisions.iterator()
                while (iterator.hasNext()) {
                    val collision = iterator.next()
                    when (collision.type) {
                        // Stop paddle from moving out of bounds. Push it back in.
                        CollisionType.BOUNDARY_LEFT -> {
                            velocity.vx = paddleSpeed
                            iterator.remove()
                        }
                        CollisionType.BOUNDARY_RIGHT -> {
                            velocity.vx = -paddleSpeed
                            iterator.remove()
                        }
                        else -> {}
                    }
                }
            }
    }
}
