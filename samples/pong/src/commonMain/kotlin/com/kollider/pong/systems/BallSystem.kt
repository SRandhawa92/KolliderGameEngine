package com.kollider.pong.systems

import com.kollider.engine.ecs.Entity
import com.kollider.engine.ecs.EntityView
import com.kollider.engine.ecs.System
import com.kollider.engine.ecs.World
import com.kollider.engine.ecs.physics.Collider
import com.kollider.engine.ecs.physics.CollisionType
import com.kollider.engine.ecs.physics.Position
import com.kollider.engine.ecs.physics.Velocity
import com.kollider.pong.components.BallComponent
import com.kollider.engine.ecs.require
import kotlin.math.abs
import kotlin.math.sqrt

fun World.ballSystem(
    worldLeft: Float,
    worldTop: Float,
    worldRight: Float,
    worldBottom: Float,
    initialSpeed: Float = 220f,
    maxSpeed: Float = 560f,
    hitSpeedIncrement: Float = 12f
) {
    addSystem(
        BallSystem(
            worldLeft,
            worldTop,
            worldRight,
            worldBottom,
            initialSpeed,
            maxSpeed,
            hitSpeedIncrement
        )
    )
}

/**
 * Pong ball behavior where:
 * - LEFT/RIGHT are walls → bounce horizontally (flip vx).
 * - TOP/BOTTOM are goals → reset to center & serve vertically.
 *
 * If you use a ResolutionSystem, set resolveBoundaries=false there for the ball
 * to avoid double handling of reflections.
 */
class BallSystem(
    private val worldLeft: Float,
    private val worldTop: Float,
    private val worldRight: Float,
    private val worldBottom: Float,
    private val initialSpeed: Float = 220f,
    private val maxSpeed: Float = 560f,
    private val hitSpeedIncrement: Float = 12f
) : System() {
    private lateinit var ballView: EntityView

    // toggles to make serves deterministic without RNG
    private var serveRightNext = true

    override fun onAttach(world: World) {
        ballView = world.view(
            BallComponent::class,
            Position::class,
            Velocity::class,
            Collider::class,
        )
    }

    @Suppress("UNUSED_PARAMETER")
    override fun update(entities: List<Entity>, deltaTime: Float) {
        for (ball in ballView) {
            val pos = ball.require<Position>()
            val vel = ball.require<Velocity>()
            val col = ball.require<Collider>()

            var flippedX = false
            var scoredTop = false
            var scoredBottom = false

            val it = col.collisions.iterator()
            while (it.hasNext()) {
                val evt = it.next()
                when (evt.type) {
                    // Paddle / entity: reflect AWAY from the paddle on X; shape Y by impact point
                    CollisionType.ENTITY -> {
                        if (!flippedX) {
                            flippedX = true

                            val other = evt.other
                            val op = other?.get(Position::class)
                            val oc = other?.get(Collider::class)
                            if (op != null && oc != null) {
                                val ballCx = pos.x + col.width * 0.5f
                                val padCx  = op.x  + oc.width * 0.5f

                                // 1) Set vx sign so we go away from the paddle:
                                //    if ball center is left of paddle center -> move left (vx < 0), else move right (vx > 0)
                                val desiredSignX = if (ballCx < padCx) -1f else +1f
                                val speed = currentSpeed(vel).coerceAtLeast(initialSpeed)
                                vel.vx = abs(vel.vx).coerceAtLeast(initialSpeed * 0.6f) * desiredSignX

                                // 2) Shape vy based on vertical offset on the paddle ([-1, 1] approx)
                                val targetVy = (vel.vy * -0.75f)
                                if (abs(targetVy) > 1e-3f) {
                                    vel.vy = targetVy
                                }

                                // 3) Keep (or gently increase) overall speed, capped
                                scaleToSpeed(vel, (speed + hitSpeedIncrement).coerceAtMost(maxSpeed))
                            } else {
                                // Fallback: if no paddle info, still force "away" using current direction
                                vel.vx = -vel.vx
                            }
                            it.remove()
                        } else {
                            it.remove()
                        }
                    }

                    // LEFT/RIGHT are walls → bounce horizontally, nudge inside
                    CollisionType.BOUNDARY_LEFT -> {
                        if (!flippedX && vel.vx < 0f) {
                            flippedX = true
                            vel.vx = -vel.vx
                            pos.x = worldLeft // nudge inside to avoid retrigger
                        }
                        it.remove()
                    }
                    CollisionType.BOUNDARY_RIGHT -> {
                        if (!flippedX && vel.vx > 0f) {
                            flippedX = true
                            vel.vx = -vel.vx
                            pos.x = worldRight - col.width // nudge inside
                        }
                        it.remove()
                    }

                    // TOP/BOTTOM are goals → reset & serve vertically
                    CollisionType.BOUNDARY_TOP -> {
                        scoredTop = true
                        it.remove()
                    }
                    CollisionType.BOUNDARY_BOTTOM -> {
                        scoredBottom = true
                        it.remove()
                    }
                }
            }

            // Handle scoring (TOP/BOTTOM) after consuming all events
            if (scoredTop || scoredBottom) {
                // center the ball
                val cx = (worldLeft + worldRight) * 0.5f
                val cy = (worldTop + worldBottom) * 0.5f
                pos.x = cx - col.width * 0.5f
                pos.y = cy - col.height * 0.5f

                // Serve back into the field vertically:
                // If it exited TOP, serve downward (+Y). If it exited BOTTOM, serve upward (-Y).
                val dirY = if (scoredTop) +1f else -1f
                serveVertical(vel, dirY)
                continue
            }

            // keep speed within bounds (guard against drift)
            clampSpeed(vel, min = initialSpeed, max = maxSpeed)
        }
    }

    private fun serveVertical(vel: Velocity, dirY: Float) {
        val base = initialSpeed
        // add a small, alternating horizontal component so serves aren't perfectly vertical
        val vx = base * 0.45f * (if (serveRightNext) 1f else -1f)
        serveRightNext = !serveRightNext

        val vy = base * dirY
        vel.vx = vx
        vel.vy = vy
        scaleToSpeed(vel, base)
    }

    private fun currentSpeed(v: Velocity): Float {
        val s2 = v.vx * v.vx + v.vy * v.vy
        return sqrt(s2.toDouble()).toFloat()
    }

    private fun scaleToSpeed(v: Velocity, target: Float) {
        val s = currentSpeed(v)
        if (s < 1e-3f) {
            v.vx = target
            v.vy = 0f
            return
        }
        val k = target / s
        v.vx *= k
        v.vy *= k
    }

    private fun clampSpeed(v: Velocity, min: Float, max: Float) {
        val s = currentSpeed(v)
        when {
            s < min -> scaleToSpeed(v, min)
            s > max -> scaleToSpeed(v, max)
        }
    }
}
