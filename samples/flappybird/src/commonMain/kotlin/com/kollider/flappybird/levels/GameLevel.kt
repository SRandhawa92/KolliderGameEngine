package com.kollider.flappybird.levels

import com.kollider.engine.core.GameConfig
import com.kollider.engine.core.SceneScope
import com.kollider.flappybird.FlappyBirdGameState
import com.kollider.flappybird.prefabs.bird
import com.kollider.flappybird.prefabs.scoreboard
import com.kollider.flappybird.systems.birdSystem
import com.kollider.flappybird.systems.obstacleSystem
import com.kollider.flappybird.systems.scoreSystem

fun SceneScope.gameLevel(
    config: GameConfig,
    state: FlappyBirdGameState
) {
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
