package com.kollider.pong

import com.kollider.engine.core.Game
import com.kollider.engine.core.GameContext
import com.kollider.engine.core.createKolliderGame
import com.kollider.pong.scenes.PongStartMenuScene

/**
 * A simple Pong game.
 *
 * @param context the game context.
 */
class Pong(private val context: GameContext) : Game(context) {

    init {
        context.engine.pushScene(PongStartMenuScene(context.config))
    }

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
                Pong(gameContext)
            }
        }
    }
}
