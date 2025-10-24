package com.kollider.flappybird

import com.kollider.engine.core.Game
import com.kollider.engine.core.GameContext
import com.kollider.engine.core.createKolliderGame
import com.kollider.flappybird.scenes.FlappyBirdGameplayScene

/**
 * A simple Flappy Bird game.
 *
 * @param context the game context.
 */
class FlappyBird(private val context: GameContext) : Game(context) {

    init {
        context.engine.pushScene(FlappyBirdGameplayScene(context.config))
    }

    companion object {
        /**
         * Creates and starts the Flappy Bird game.
         */
        fun createGame(screenHeight: Int = 800, screenWidth: Int = 600) {
            createKolliderGame {
                title = "Flappy Bird"
                width = screenWidth
                height = screenHeight
            }.start { gameContext ->
                FlappyBird(gameContext)
            }
        }
    }
}
