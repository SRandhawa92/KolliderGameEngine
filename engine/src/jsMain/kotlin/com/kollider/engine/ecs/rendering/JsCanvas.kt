package com.kollider.engine.ecs.rendering

import com.kollider.engine.core.GameConfig

actual fun createCanvas(config: GameConfig): Canvas {
    return JsCanvas(config.height, config.width)
}

class JsCanvas(
    override val canvasHeight: Int,
    override val canvasWidth: Int
): Canvas {
    override fun paint() {
        // Paint the canvas on JS
    }
}