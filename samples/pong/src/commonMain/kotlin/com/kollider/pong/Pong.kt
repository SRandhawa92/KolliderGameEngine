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
        fun createGame(
            virtualWidth: Int = 800,
            virtualHeight: Int = 600,
            renderWidth: Int? = null,
            renderHeight: Int? = null,
        ) {
            createKolliderGame {
                title = "Pong"
                width = virtualWidth
                height = virtualHeight
                renderWidth?.let { renderWidthOverride = it }
                renderHeight?.let { renderHeightOverride = it }
            }.start { gameContext ->
                Pong(gameContext)
            }
        }
    }
}
