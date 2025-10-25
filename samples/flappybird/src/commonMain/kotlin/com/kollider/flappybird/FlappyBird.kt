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
         *
         * @param virtualWidth logical width used for gameplay measurements.
         * @param virtualHeight logical height used for gameplay measurements.
         * @param renderWidth optional physical render width (defaults to [virtualWidth]).
         * @param renderHeight optional physical render height (defaults to [virtualHeight]).
         */
        fun createGame(
            virtualWidth: Int = 800,
            virtualHeight: Int = 600,
            renderWidth: Int? = null,
            renderHeight: Int? = null,
        ) {
            createKolliderGame {
                title = "Flappy Bird"
                width = virtualWidth
                height = virtualHeight
                renderWidth?.let { renderWidthOverride = it }
                renderHeight?.let { renderHeightOverride = it }
            }.start { gameContext ->
                FlappyBird(gameContext)
            }
        }
    }
}
