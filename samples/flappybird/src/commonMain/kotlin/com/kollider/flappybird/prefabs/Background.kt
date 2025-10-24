package com.kollider.flappybird.prefabs

import com.kollider.engine.core.GameConfig
import com.kollider.engine.core.SceneScope
import com.kollider.engine.ecs.physics.Position
import com.kollider.engine.ecs.rendering.Drawable

fun SceneScope.background(config: GameConfig) {
    createEntity {
        add(Position(0f, 0f))
        add(Drawable.Rect(
            width = config.width.toFloat(),
            height = config.height.toFloat(),
            color = 0xFF87CEEB.toInt() // sky blue; update to your palette
        ))
    }
}