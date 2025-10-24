package com.kollider.flappybird.systems

import com.kollider.engine.core.GameConfig
import com.kollider.engine.ecs.Entity
import com.kollider.engine.ecs.System
import com.kollider.engine.ecs.World
import com.kollider.engine.ecs.EntityView
import com.kollider.engine.ecs.input.InputComponent
import com.kollider.engine.ecs.physics.Collider
import com.kollider.engine.ecs.physics.CollisionType
import com.kollider.engine.ecs.physics.Position
import com.kollider.engine.ecs.physics.Velocity
import com.kollider.engine.ecs.require
import com.kollider.flappybird.FlappyBirdGameState
import com.kollider.flappybird.components.BirdComponent
import com.kollider.flappybird.components.ObstacleComponent
import com.kollider.flappybird.prefabs.bird

private const val BIRD_START_X = 100f

fun World.birdSystem(
    jumpSpeed: Float,
    gravity: Float,
    config: GameConfig,
    state: FlappyBirdGameState,
) {
    addSystem(BirdSystem(jumpSpeed, gravity, config, this, state))
}

class BirdSystem(
    private val jumpSpeed: Float,
    private val gravity: Float,
    private val config: GameConfig,
    override val world: World,
    private val state: FlappyBirdGameState,
): System() {
    private lateinit var birdView: EntityView
    private lateinit var obstacleView: EntityView

    override fun onAttach(world: World) {
        birdView = world.view(BirdComponent::class)
        obstacleView = world.view(ObstacleComponent::class)
    }

    override fun update(entities: List<Entity>, deltaTime: Float) {
        if (!state.isRunning) {
            if (state.tickRestart(deltaTime)) {
                restartRun(entities)
            }
            return
        }

        var hitObstacle = false

        birdView.forEach { bird ->
            val velocity = bird.require<Velocity>()
            val input = bird.require<InputComponent>()
            val collider = bird.require<Collider>()

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
            state.enterGameOver()
            freezeWorld(entities)
        }
    }

    private fun freezeWorld(entities: List<Entity>) {
        obstacleView.forEach { obstacle ->
            obstacle.get(Velocity::class)?.apply {
                vx = 0f
                vy = 0f
            }
        }

        birdView.forEach { bird ->
            bird.get(Velocity::class)?.apply {
                vx = 0f
                vy = 0f
            }
        }
    }

    private fun restartRun(entities: List<Entity>) {
        // Remove existing obstacles.
        obstacleView.toList().forEach { world.removeEntity(it) }

        val bird = birdView.toList().firstOrNull()
        if (bird == null) {
            world.bird(config)
            state.markRestarted()
            return
        }

        val position = bird.require<Position>()
        val velocity = bird.require<Velocity>()
        val collider = bird.require<Collider>()
        val input = bird.require<InputComponent>()

        position.x = BIRD_START_X
        position.y = config.height / 2f

        velocity.vx = 0f
        velocity.vy = 0f

        input.shoot = false

        collider.collisions.clear()

        state.markRestarted()
    }
}
