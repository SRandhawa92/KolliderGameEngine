package com.kollider.pong.prefabs

import com.kollider.engine.core.GameConfig
import com.kollider.engine.core.SceneScope
import com.kollider.engine.ecs.physics.Position
import com.kollider.engine.ecs.rendering.Drawable
import com.kollider.engine.ecs.require
import com.kollider.pong.components.HighScoreComponent
import com.kollider.pong.components.HIGH_SCORE_STORAGE_KEY
import com.kollider.pong.components.ScoreComponent
import kotlinx.coroutines.launch

private const val SCORE_COLOR = 0xFFFFFFFF.toInt()
private const val SCORE_TEXT_SIZE = 28f
private const val HIGH_SCORE_TEXT_SIZE = 22f

internal fun formatScoreText(player: Int, computer: Int): String =
    "Player $player  |  CPU $computer"

internal fun formatHighScoreText(score: Int): String = "High Score: $score"

/**
 * Spawns a scoreboard that keeps track of both player and computer points.
 */
fun SceneScope.scoreboard(config: GameConfig) {
    createEntity {
        add(Position(config.width / 2f - 130f, 24f))
        add(Drawable.Text(formatScoreText(0, 0), SCORE_TEXT_SIZE, SCORE_COLOR))
        add(ScoreComponent())
    }

    val highScoreEntity = createEntity {
        add(Position(config.width / 2f - 130f, 56f))
        add(Drawable.Text(formatHighScoreText(0), HIGH_SCORE_TEXT_SIZE, SCORE_COLOR))
        add(HighScoreComponent())
    }

    val storage = context.storage
    config.scope.launch {
        val stored = storage.getInt(HIGH_SCORE_STORAGE_KEY, 0)
        val component = highScoreEntity.require<HighScoreComponent>()
        component.best = stored
        updateHighScoreDrawable(highScoreEntity, stored)
    }
}

internal fun updateHighScoreDrawable(entity: com.kollider.engine.ecs.Entity, score: Int) {
    val drawable = entity.get(Drawable::class)
    if (drawable is Drawable.Text) {
        val text = formatHighScoreText(score)
        if (drawable.text != text) {
            entity.add(drawable.copy(text = text))
        }
    }
}
