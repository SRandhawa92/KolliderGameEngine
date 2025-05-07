package com.kollider.pong

import com.kollider.engine.core.Game
import com.kollider.engine.core.GameContext
import com.kollider.engine.core.createKolliderGame
import com.kollider.pong.levels.startMenuLevel
import com.kollider.pong.prefabs.ball
import com.kollider.pong.prefabs.computerPaddle
import com.kollider.pong.prefabs.playerPaddle
import com.kollider.pong.systems.BallSystem
import com.kollider.pong.systems.ComputerPaddleSystem
import com.kollider.pong.systems.PlayerPaddleSystem
import com.kollider.pong.systems.startMenuSystem

/**
 * A simple Pong game.
 *
 * @param context the game context.
 */
class Pong(context: GameContext): Game(context) {

    companion object {
        /**
         * Creates and starts the Pong game.
         */
        fun createGame(screenHeight: Int = 800, screenWidth: Int = 600) {
            createKolliderGame {
                title = "Pong"
                width = screenWidth
                height = screenHeight
            }.start { gameContext ->
                gameContext.world.startMenuLevel(gameContext.config)
                Pong(gameContext)
            }
        }
    }
}
