package com.kollider.flappybird.systems

import com.kollider.engine.core.GameConfig
import com.kollider.engine.ecs.Entity
import com.kollider.engine.ecs.System
import com.kollider.engine.ecs.World
import com.kollider.engine.ecs.physics.Position
import com.kollider.engine.ecs.require
import com.kollider.engine.ecs.withAll
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
    addSystem(ObstacleSystem(speed, spawnIntervalSeconds, gapPadding, this, config, state))
}

class ObstacleSystem(
    private val speed: Float,
    private val spawnIntervalSeconds: Float,
    private val gapPadding: Float,
    private val world: World,
    private val config: GameConfig,
    private val state: FlappyBirdGameState,
): System() {
    private var timeSinceLastObstacle = 0f

    override fun update(entities: List<Entity>, deltaTime: Float) {
        if (!state.isRunning) {
            timeSinceLastObstacle = 0f
            return
        }

        timeSinceLastObstacle += deltaTime

        // Spawn new obstacles and remove old ones that are out of the screen
        entities.withAll(ObstacleComponent::class).forEach { obstacle ->
            val position = obstacle.require<Position>()

            // Remove obstacle if it's out of the screen
            if (position.x < -50) world.removeEntity(obstacle)
        }

        // Spawn new obstacle every interval
        if (timeSinceLastObstacle >= spawnIntervalSeconds) {
            world.obstacle(speed, config, gapPadding)
            timeSinceLastObstacle = 0f
        }
    }
}
