package com.kollider.pong.prefabs

import com.kollider.engine.core.GameConfig
import com.kollider.engine.ecs.World
import com.kollider.engine.ecs.physics.Position
import com.kollider.engine.ecs.rendering.Drawable
import com.kollider.pong.components.ScoreComponent

private const val SCORE_COLOR = 0xFFFFFFFF.toInt()
private const val SCORE_TEXT_SIZE = 28f

internal fun formatScoreText(player: Int, computer: Int): String =
    "Player $player  |  CPU $computer"

/**
 * Spawns a scoreboard that keeps track of both player and computer points.
 */
fun World.scoreboard(config: GameConfig) {
    createEntity().apply {
        add(Position(config.width / 2f - 130f, 24f))
        add(Drawable.Text(formatScoreText(0, 0), SCORE_TEXT_SIZE, SCORE_COLOR))
        add(ScoreComponent())
    }
}
