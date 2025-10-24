package com.kollider.flappybird.systems

import com.kollider.engine.core.GameConfig
import com.kollider.engine.core.SceneScope
import com.kollider.engine.ecs.Entity
import com.kollider.engine.ecs.EntityView
import com.kollider.engine.ecs.System
import com.kollider.engine.ecs.World
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
private const val BIRD_SIZE = 50f

fun SceneScope.birdSystem(
    jumpSpeed: Float,
    gravity: Float,
    config: GameConfig,
    state: FlappyBirdGameState,
) {
    addSystem(BirdSystem(jumpSpeed, gravity, config, state) { bird(config) })
}

class BirdSystem(
    private val jumpSpeed: Float,
    private val gravity: Float,
    private val config: GameConfig,
    private val state: FlappyBirdGameState,
    private val spawnBird: () -> Entity,
): System() {
    private lateinit var birdView: EntityView
    private lateinit var obstacleView: EntityView

    override fun onAttach(world: World) {
        birdView = world.view(BirdComponent::class)
        obstacleView = world.view(ObstacleComponent::class)
    }

    @Suppress("UNUSED_PARAMETER")
    override fun update(entities: List<Entity>, deltaTime: Float) {
        if (!state.isRunning) {
            if (state.tickRestart(deltaTime)) {
                restartRun()
            }
            return
        }

        var hitObstacle = false

        birdView.forEach { bird ->
            val velocity = bird.require<Velocity>()
            val input = bird.require<InputComponent>()
            val collider = bird.require<Collider>()
            val position = bird.require<Position>()

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

            // Clamp bird within vertical bounds of the screen.
            if (position.y < 0f) {
                position.y = 0f
                if (velocity.vy < 0f) velocity.vy = 0f
            }
            val maxY = (config.height.toFloat() - BIRD_SIZE).coerceAtLeast(0f)
            if (position.y > maxY) {
                position.y = maxY
                if (velocity.vy > 0f) velocity.vy = 0f
            }
        }

        if (hitObstacle) {
            state.enterGameOver()
            freezeWorld()
        }
    }

    private fun freezeWorld() {
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

    private fun restartRun() {
        // Remove existing obstacles.
        val gameWorld = world
        obstacleView.toList().forEach { gameWorld.removeEntity(it) }

        val bird = birdView.toList().firstOrNull()
        if (bird == null) {
            spawnBird()
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
