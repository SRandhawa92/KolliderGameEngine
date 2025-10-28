package com.kollider.engine.ecs.rendering

import com.kollider.engine.core.GameConfig
import com.kollider.engine.ios.IosCanvasHost
import com.kollider.engine.ios.IosCanvasSize
import com.kollider.engine.ios.IosPlatform

actual fun createCanvas(config: GameConfig): Canvas {
    val host = IosPlatform.canvasHost
        ?: error("IosPlatform.canvasHost must be installed before creating the game.")

    val fallbackSize = IosCanvasSize(
        width = config.renderWidth,
        height = config.renderHeight,
    )
    val initialSize = host.currentSize().takeIf { it.width > 0 && it.height > 0 } ?: fallbackSize

    return IosCanvas(
        host = host,
        initialSize = initialSize,
    )
}

class IosCanvas(
    private val host: IosCanvasHost,
    initialSize: IosCanvasSize,
) : Canvas {
    private var width: Int = initialSize.width
    private var height: Int = initialSize.height

    override val canvasWidth: Int get() = width
    override val canvasHeight: Int get() = height

    override fun paint() {
        host.requestRender()
    }

    internal fun updateSize(newWidth: Int, newHeight: Int) {
        if (newWidth == width && newHeight == height) return
        width = newWidth
        height = newHeight
        host.onResize(newWidth, newHeight)
    }
}
