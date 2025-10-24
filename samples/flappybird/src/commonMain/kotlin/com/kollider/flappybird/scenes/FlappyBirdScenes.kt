package com.kollider.flappybird.scenes

import com.kollider.engine.core.GameConfig
import com.kollider.engine.core.Scene
import com.kollider.engine.core.SceneScope
import com.kollider.flappybird.FlappyBirdGameState
import com.kollider.flappybird.levels.gameLevel

class FlappyBirdGameplayScene(
    private val config: GameConfig
) : Scene {
    private val state = FlappyBirdGameState(
        restartDelaySeconds = 1.5f,
        onGameOver = { println("Flappy Bird: game over") },
        onRestart = { println("Flappy Bird: restarting run") },
    )

    override fun onEnter(scope: SceneScope) {
        scope.gameLevel(config, state)
    }
}
