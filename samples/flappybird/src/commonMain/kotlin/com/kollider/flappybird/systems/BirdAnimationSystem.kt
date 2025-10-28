package com.kollider.flappybird.systems

import com.kollider.engine.core.SceneScope
import com.kollider.engine.ecs.Entity
import com.kollider.engine.ecs.EntityView
import com.kollider.engine.ecs.System
import com.kollider.engine.ecs.World
import com.kollider.engine.ecs.physics.Vector2
import com.kollider.engine.ecs.require
import com.kollider.flappybird.FlappyBirdGameState
import com.kollider.flappybird.components.BirdAnimationComponent
import kotlin.math.PI

private val TWO_PI = (2.0 * PI).toFloat()

fun SceneScope.birdAnimationSystem(
    flapSpeed: Float = 8f,
    flapAmplitude: Float = 1.55f,
    state: FlappyBirdGameState,
) {
    addSystem(BirdAnimationSystem(flapSpeed, flapAmplitude, state))
}


class BirdAnimationSystem(
    private val flapSpeed: Float,
    private val flapAmplitude: Float,
    private val state: FlappyBirdGameState,
) : System() {
    private lateinit var birdAnim: EntityView

    override fun onAttach(world: World) {
        birdAnim = world.view(BirdAnimationComponent::class)
    }

    override fun update(entities: List<Entity>, deltaTime: Float) {
        if (!state.isRunning) return

        birdAnim.forEach { bird ->
            val anim = bird.require<BirdAnimationComponent>()
            anim.phase = (anim.phase + flapSpeed * deltaTime) % TWO_PI
            val flap = (0.5f + 0.5f * kotlin.math.sin(anim.phase)) * flapAmplitude
            applyFlap(anim, flap)
        }
    }

    private fun applyFlap(anim: BirdAnimationComponent, flap: Float) {
        val restWing = anim.restPose
        val targetWing = anim.wingShape

        // fix anchor point at body-wing joint
        adjustPoint(targetWing[0], restWing[0], 0f)
        adjustPoint(targetWing[1], restWing[1], 0f)

        // move tip of wing up and down
        val tipDeltaY = (restWing[2].y - restWing[1].y) * flap
        adjustPoint(targetWing[2], restWing[2], tipDeltaY)
    }

    private fun adjustPoint(target: Vector2, rest: Vector2, deltaY: Float) {
        target.x = rest.x
        target.y = rest.y + deltaY
    }
}