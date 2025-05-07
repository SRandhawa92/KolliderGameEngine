package com.kollider.flappybird.systems

import com.kollider.engine.core.GameConfig
import com.kollider.engine.ecs.Entity
import com.kollider.engine.ecs.System
import com.kollider.engine.ecs.World
import com.kollider.engine.ecs.physics.Collider
import com.kollider.engine.ecs.physics.CollisionType
import com.kollider.engine.ecs.physics.Position
import com.kollider.flappybird.components.ObstacleComponent
import com.kollider.flappybird.prefabs.obstacle

fun World.obstacleSystem(speed: Float, spawnInterval: Int, config: GameConfig) {
    addSystem(ObstacleSystem(speed, spawnInterval, this, config))
}

class ObstacleSystem(
    private val speed: Float,
    private val spawnInterval: Int,
    private val world: World,
    private val config: GameConfig
): System() {
    private var timeSinceLastObstacle = 0f

    override fun update(entities: List<Entity>, deltaTime: Float) {
        timeSinceLastObstacle += deltaTime

        // Spawn new obstacles and remove old ones that are out of the screen
        entities.filter { it.has(ObstacleComponent::class) }.forEach { obstacle ->
            val position = obstacle.get(Position::class)!!

            // Remove obstacle if it's out of the screen
            if (position.x < -50) world.removeEntity(obstacle)
        }

        // Spawn new obstacle every interval
        if (timeSinceLastObstacle > spawnInterval) {
            world.obstacle(speed, config)
            timeSinceLastObstacle = 0f
        }
    }
}