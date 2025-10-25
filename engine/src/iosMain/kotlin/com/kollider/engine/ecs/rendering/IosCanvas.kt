package com.kollider.engine.ecs.rendering

import com.kollider.engine.core.GameConfig

actual fun createCanvas(config: GameConfig): Canvas {
    return IosCanvas(config.renderHeight, config.renderWidth)
}

class IosCanvas(
    override val canvasHeight: Int,
    override val canvasWidth: Int
): Canvas {
    override fun paint() {
        // Paint the canvas on iOS
    }
}
