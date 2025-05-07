package com.kollider.pong.systems

import com.kollider.engine.ecs.Entity
import com.kollider.engine.ecs.System
import com.kollider.engine.ecs.World
import com.kollider.engine.ecs.physics.Position
import com.kollider.engine.ecs.physics.Velocity
import com.kollider.pong.components.BallComponent
import com.kollider.pong.components.ComputerPaddleComponent

fun World.computerPaddleSystem(paddleSpeed: Float) {
    addSystem(ComputerPaddleSystem(paddleSpeed))
}

/**
 * System that controls the AI paddle by following the ball.
 */
class ComputerPaddleSystem(
    private val paddleSpeed: Float
) : System() {
    override fun update(entities: List<Entity>, deltaTime: Float) {
        // Locate the ball entity.
        val ball = entities.firstOrNull { it.has(BallComponent::class) }!!
        val ballPos = ball.get(Position::class)!!

        // For every AI-controlled paddle, adjust the velocity based on the ball's vertical position.
        entities.filter { it.has(ComputerPaddleComponent::class) }
            .forEach { paddle ->
                val pos = paddle.get(Position::class)!!
                val velocity = paddle.get(Velocity::class)!!

                // No need to check for collisions here, as the AI paddle can't move out of bounds.
                when {
                    pos.x + 50 < ballPos.x -> velocity.vx = paddleSpeed
                    pos.x + 50 > ballPos.x -> velocity.vx = -paddleSpeed
                    else -> velocity.vx = 0f
                }
            }
    }
}