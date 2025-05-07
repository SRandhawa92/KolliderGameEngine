package com.kollider.flappybird

import com.kollider.engine.core.Game
import com.kollider.engine.core.GameContext
import com.kollider.engine.core.createKolliderGame
import com.kollider.flappybird.levels.gameLevel

/**
 * A simple Flappy Bird game.
 *
 * @param context the game context.
 */
class FlappyBird(context: GameContext): Game(context) {

    companion object {
        /**
         * Creates and starts the Pong game.
         */
        fun createGame(screenHeight: Int = 800, screenWidth: Int = 600) {
            createKolliderGame {
                title = "Flappy Bird"
                width = screenWidth
                height = screenHeight
            }.start { gameContext ->
                gameContext.world.gameLevel(gameContext.config)
                FlappyBird(gameContext)
            }
        }
    }
}
