package com.kollider.pong.levels

import com.kollider.engine.core.GameConfig
import com.kollider.engine.core.SceneScope
import com.kollider.pong.prefabs.ball
import com.kollider.pong.prefabs.computerPaddle
import com.kollider.pong.prefabs.playerPaddle
import com.kollider.pong.prefabs.scoreboard
import com.kollider.pong.systems.ballSystem
import com.kollider.pong.systems.computerPaddleSystem
import com.kollider.pong.systems.playerPaddleSystem

fun SceneScope.pongLevel(config: GameConfig) {
    ball(config)
    playerPaddle(config)
    computerPaddle(config)
    scoreboard(config)

    ballSystem(0f, 0f, config.width.toFloat(), config.height.toFloat())
    playerPaddleSystem(150f)
    computerPaddleSystem(250f)
}
