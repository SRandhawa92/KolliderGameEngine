package com.kollider.pong.systems

import com.kollider.engine.ecs.Entity
import com.kollider.engine.ecs.System
import com.kollider.engine.ecs.World
import com.kollider.engine.ecs.physics.Collider
import com.kollider.engine.ecs.physics.CollisionType
import com.kollider.engine.ecs.physics.Position
import com.kollider.engine.ecs.physics.Velocity
import com.kollider.pong.components.BallComponent

fun World.ballSystem(screenWidth: Int, screenHeight: Int) {
    addSystem(BallSystem(screenWidth, screenHeight))
}

class BallSystem(
    private val screenWidth: Int,
    private val screenHeight: Int
) : System() {
    override fun update(entities: List<Entity>, deltaTime: Float) {
        // Process ball entities.
        entities.filter { it.has(BallComponent::class) }.forEach { ball ->
            val position = ball.get(Position::class)!!
            val velocity = ball.get(Velocity::class)!!
            val collider = ball.get(Collider::class)!!

            // Handle collisions.
            val iterator = collider.collisions.iterator()
            while (iterator.hasNext()) {
                val collision = iterator.next()
                when (collision.type) {
                    // Reverse the ball's horizontal velocity.
                    CollisionType.BOUNDARY_LEFT, CollisionType.BOUNDARY_RIGHT -> {
                        if (collision.type == CollisionType.BOUNDARY_LEFT && velocity.vx < 0 ||
                            collision.type == CollisionType.BOUNDARY_RIGHT && velocity.vx > 0) {
                            velocity.vx = -velocity.vx
                        }
                    }

                    // Reset the ball position.
                    CollisionType.BOUNDARY_TOP, CollisionType.BOUNDARY_BOTTOM -> {
                        position.x = screenWidth / 2f
                        position.y = screenHeight / 2f
                        velocity.vx = -velocity.vx
                        velocity.vy = 150f
                        iterator.remove()
                    }

                    // Reverse the ball's vertical velocity.
                    CollisionType.ENTITY -> {
                        velocity.vy = -velocity.vy
                        iterator.remove()
                    }
                }
            }
        }
    }
}