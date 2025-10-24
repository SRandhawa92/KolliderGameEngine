package com.kollider.flappybird.prefabs

import com.kollider.engine.core.GameConfig
import com.kollider.engine.core.SceneScope
import com.kollider.engine.ecs.physics.Position
import com.kollider.engine.ecs.rendering.Drawable
import com.kollider.flappybird.components.ScoreComponent

private const val SCORE_TEXT_COLOR = 0xFFFFFFFF.toInt()
private const val SCORE_TEXT_SIZE = 28f
private const val SCORE_OFFSET_X = -90f
private const val SCORE_OFFSET_Y = 20f

internal fun formatScoreText(score: Int): String = "Score: $score"

/**
 * Adds a scoreboard entity that displays the current score.
 */
fun SceneScope.scoreboard(config: GameConfig) {
    createEntity {
        add(Position(config.width / 2f + SCORE_OFFSET_X, SCORE_OFFSET_Y))
        add(Drawable.Text(formatScoreText(0), SCORE_TEXT_SIZE, SCORE_TEXT_COLOR))
        add(ScoreComponent())
    }
}
