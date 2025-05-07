package com.kollider.flappybird.levels

import com.kollider.engine.core.GameConfig
import com.kollider.engine.ecs.World
import com.kollider.flappybird.prefabs.bird
import com.kollider.flappybird.systems.birdSystem
import com.kollider.flappybird.systems.obstacleSystem

fun World.gameLevel(config: GameConfig) {
    bird(config = config)
    birdSystem(jumpSpeed = -50f, gravity = 50f, config = config)
    obstacleSystem(speed = -100f, spawnInterval = 3, config = config)
}