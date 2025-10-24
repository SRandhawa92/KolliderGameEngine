package com.kollider.flappybird.systems

import com.kollider.engine.core.GameConfig
import com.kollider.engine.ecs.Entity
import com.kollider.engine.ecs.System
import com.kollider.engine.ecs.World
import com.kollider.engine.ecs.EntityView
import com.kollider.engine.ecs.physics.Position
import com.kollider.engine.ecs.require
import com.kollider.flappybird.FlappyBirdGameState
import com.kollider.flappybird.components.ObstacleComponent
import com.kollider.flappybird.prefabs.obstacle

fun World.obstacleSystem(
    speed: Float,
    spawnIntervalSeconds: Float,
    gapPadding: Float,
    config: GameConfig,
    state: FlappyBirdGameState,
) {
    addSystem(ObstacleSystem(speed, spawnIntervalSeconds, gapPadding, config, state))
}

class ObstacleSystem(
    private val speed: Float,
    private val spawnIntervalSeconds: Float,
    private val gapPadding: Float,
    private val config: GameConfig,
    private val state: FlappyBirdGameState,
): System() {
    private var timeSinceLastObstacle = 0f
    private lateinit var obstacleView: EntityView

    override fun onAttach(world: World) {
        obstacleView = world.view(ObstacleComponent::class)
    }

    @Suppress("UNUSED_PARAMETER")
    override fun update(entities: List<Entity>, deltaTime: Float) {
        if (!state.isRunning) {
            timeSinceLastObstacle = 0f
            return
        }

        timeSinceLastObstacle += deltaTime

        // Spawn new obstacles and remove old ones that are out of the screen
        val gameWorld = world
        obstacleView.forEach { obstacle ->
            val position = obstacle.require<Position>()

            // Remove obstacle if it's out of the screen
            if (position.x < -50) gameWorld.removeEntity(obstacle)
        }

        // Spawn new obstacle every interval
        if (timeSinceLastObstacle >= spawnIntervalSeconds) {
            gameWorld.obstacle(speed, config, gapPadding)
            timeSinceLastObstacle = 0f
        }
    }
}
