package com.kollider.flappybird.levels

import com.kollider.engine.core.GameConfig
import com.kollider.engine.ecs.World
import com.kollider.flappybird.FlappyBirdGameState
import com.kollider.flappybird.prefabs.bird
import com.kollider.flappybird.prefabs.scoreboard
import com.kollider.flappybird.systems.birdSystem
import com.kollider.flappybird.systems.obstacleSystem
import com.kollider.flappybird.systems.scoreSystem

fun World.gameLevel(config: GameConfig) {
    val state = FlappyBirdGameState(
        restartDelaySeconds = 1.5f,
        onGameOver = { println("Flappy Bird: game over") },
        onRestart = { println("Flappy Bird: restarting run") },
    )

    scoreboard(config = config)
    bird(config = config)
    birdSystem(jumpSpeed = -50f, gravity = 50f, config = config, state = state)
    obstacleSystem(
        speed = -100f,
        spawnIntervalSeconds = 3f,
        gapPadding = 120f,
        config = config,
        state = state,
    )
    scoreSystem(state = state)
}
