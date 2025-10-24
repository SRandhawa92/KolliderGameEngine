package com.kollider.flappybird.systems

import com.kollider.engine.core.GameConfig
import com.kollider.engine.core.SceneScope
import com.kollider.engine.ecs.Entity
import com.kollider.engine.ecs.EntityView
import com.kollider.engine.ecs.System
import com.kollider.engine.ecs.World
import com.kollider.engine.ecs.physics.Position
import com.kollider.engine.ecs.require
import com.kollider.flappybird.FlappyBirdGameState
import com.kollider.flappybird.components.CloudComponent
import com.kollider.flappybird.prefabs.cloud

fun SceneScope.cloudSystem(
    speed: Float,
    state: FlappyBirdGameState,
    spawnIntervalSeconds: Float,
    gapPadding: Float,
    config: GameConfig,
) {
   addSystem(
       CloudSystem(
           state,
           spawnIntervalSeconds
       ) { cloud(speed, config, gapPadding) }
   )
}

class CloudSystem(
    private val state: FlappyBirdGameState,
    private val spawnIntervalSeconds: Float,
    private val spawnCloud: () -> Entity,
): System() {
    private var timeSinceLastCloud = 0f
    private lateinit var cloudView: EntityView

    override fun onAttach(world: World) {
        cloudView = world.view(CloudComponent::class)
    }

    override fun update(entities: List<Entity>, deltaTime: Float) {
        if (!state.isRunning) {
            timeSinceLastCloud = 0f
            return
        }

        timeSinceLastCloud += deltaTime

        // Spawn new clouds and remove old ones that are out of the screen
        val gameWorld = world
        cloudView.forEach { cloud ->
            val position = cloud.require<Position>()

            // Remove cloud if it's out of the screen
            if (position.x < -100) gameWorld.removeEntity(cloud)
        }

        // Spawn new cloud every interval
        if (timeSinceLastCloud >= spawnIntervalSeconds) {
            spawnCloud()
            timeSinceLastCloud = 0f
        }
    }
}