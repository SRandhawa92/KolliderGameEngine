package com.kollider.flappybird.prefabs

import com.kollider.engine.core.GameConfig
import com.kollider.engine.core.SceneScope
import com.kollider.engine.ecs.Entity
import com.kollider.engine.ecs.physics.Position
import com.kollider.engine.ecs.rendering.Drawable
import com.kollider.engine.ecs.require
import com.kollider.flappybird.components.FLAPPY_HIGH_SCORE_KEY
import com.kollider.flappybird.components.HighScoreComponent
import com.kollider.flappybird.components.ScoreComponent
import kotlinx.coroutines.launch

private const val SCORE_TEXT_COLOR = 0xFFFFFFFF.toInt()
private const val SCORE_TEXT_SIZE = 28f
private const val FRAME_PADDING_X = 20f
private const val FRAME_PADDING_Y = 0f
private const val SCORE_OFFSET_Y = 32f
private const val HIGH_SCORE_OFFSET_Y = SCORE_OFFSET_Y + 32f
private const val SCORE_WIDTH = 220f
private const val SCORE_HEIGHT = 108f
private const val FRAME_COLOR = 0x66000000
private const val HIGH_SCORE_TEXT_SIZE = 20f
private const val HIGH_SCORE_COLOR = 0xFFB3E5FC.toInt()

internal fun formatScoreText(score: Int): String = "Score: $score"

internal fun formatHighScoreText(score: Int): String = "Best: $score"

/**
 * Adds a scoreboard entity that displays the current score.
 */
fun SceneScope.scoreboard(config: GameConfig) {
    val frameX = config.width / 2f - SCORE_WIDTH / 2f
    val frameY = SCORE_OFFSET_Y - SCORE_TEXT_SIZE * 0.5f - FRAME_PADDING_Y

    createEntity {
        add(Position(frameX, frameY))
        add(Drawable.Rect(SCORE_WIDTH, SCORE_HEIGHT, FRAME_COLOR, offsetX = 0f, offsetY = 0f))
    }

    createEntity {
        add(Position(frameX + FRAME_PADDING_X, frameY + FRAME_PADDING_Y + SCORE_TEXT_SIZE))
        add(Drawable.Text(formatScoreText(0), SCORE_TEXT_SIZE, SCORE_TEXT_COLOR))
        add(ScoreComponent())
    }

    val highScoreEntity = createEntity {
        add(Position(frameX + FRAME_PADDING_X, frameY + FRAME_PADDING_Y + SCORE_TEXT_SIZE + 30f))
        add(Drawable.Text(formatHighScoreText(0), HIGH_SCORE_TEXT_SIZE, HIGH_SCORE_COLOR))
        add(HighScoreComponent())
    }

    val storage = context.storage
    config.scope.launch {
        val stored = storage.getInt(FLAPPY_HIGH_SCORE_KEY, 0)
        val component = highScoreEntity.require<HighScoreComponent>()
        component.best = stored
        updateHighScoreDrawable(highScoreEntity, stored)
    }
}

internal fun updateHighScoreDrawable(entity: Entity, score: Int) {
    val drawable = entity.get(Drawable::class)
    if (drawable is Drawable.Text) {
        val text = formatHighScoreText(score)
        if (drawable.text != text) {
            entity.add(drawable.copy(text = text))
        }
    }
}
