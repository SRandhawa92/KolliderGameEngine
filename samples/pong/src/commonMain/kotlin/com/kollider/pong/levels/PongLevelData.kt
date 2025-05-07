package com.kollider.pong.levels

import com.kollider.engine.core.GameConfig
import com.kollider.engine.ecs.World
import com.kollider.pong.prefabs.ball
import com.kollider.pong.prefabs.computerPaddle
import com.kollider.pong.prefabs.playerPaddle
import com.kollider.pong.systems.ballSystem
import com.kollider.pong.systems.computerPaddleSystem
import com.kollider.pong.systems.playerPaddleSystem

fun World.pongLevel(config: GameConfig) {
    ball(config)
    playerPaddle(config)
    computerPaddle(config)

    ballSystem(config.width, config.height)
    playerPaddleSystem(150f)
    computerPaddleSystem(250f)
}