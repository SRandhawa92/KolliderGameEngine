package com.kollider.pong.systems

import com.kollider.engine.ecs.Entity
import com.kollider.engine.ecs.System
import com.kollider.engine.ecs.World
import com.kollider.engine.ecs.EntityView
import com.kollider.engine.ecs.physics.Position
import com.kollider.engine.ecs.physics.Velocity
import com.kollider.pong.components.BallComponent
import com.kollider.pong.components.ComputerPaddleComponent
import com.kollider.engine.ecs.require

fun World.computerPaddleSystem(paddleSpeed: Float) {
    addSystem(ComputerPaddleSystem(paddleSpeed))
}

/**
 * System that controls the AI paddle by following the ball.
 */
class ComputerPaddleSystem(
    private val paddleSpeed: Float
) : System() {
    private lateinit var ballView: EntityView
    private lateinit var paddleView: EntityView

    override fun onAttach(world: World) {
        ballView = world.view(BallComponent::class, Position::class)
        paddleView = world.view(ComputerPaddleComponent::class, Position::class, Velocity::class)
    }

    @Suppress("UNUSED_PARAMETER")
    override fun update(entities: List<Entity>, deltaTime: Float) {
        // Locate the ball entity.
        val ballIterator = ballView.iterator()
        if (!ballIterator.hasNext()) return
        val ball = ballIterator.next()
        val ballPos = ball.require<Position>()

        // For every AI-controlled paddle, adjust the velocity based on the ball's vertical position.
        paddleView.forEach { paddle ->
            val pos = paddle.require<Position>()
            val velocity = paddle.require<Velocity>()

            // No need to check for collisions here, as the AI paddle can't move out of bounds.
            when {
                pos.x + 50 < ballPos.x -> velocity.vx = paddleSpeed
                pos.x + 50 > ballPos.x -> velocity.vx = -paddleSpeed
                else -> velocity.vx = 0f
            }
        }
    }
}
