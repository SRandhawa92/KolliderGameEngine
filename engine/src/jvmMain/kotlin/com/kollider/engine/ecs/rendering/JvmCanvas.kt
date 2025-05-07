package com.kollider.engine.ecs.rendering

import com.kollider.engine.core.GameConfig
import java.awt.Graphics
import java.awt.image.BufferedImage
import javax.swing.JPanel

actual fun createCanvas(config: GameConfig): Canvas {
    return JvmCanvas(config.height, config.width)
}

class JvmCanvas(
    override val canvasHeight: Int,
    override val canvasWidth: Int
) : JPanel(), Canvas {

    /** The latest rendered frame. */
    private var frameBuffer: BufferedImage? = null

    override fun paint() {
        paintComponents(this.graphics)
    }

    override fun paintComponent(g: Graphics?) {
        super.paintComponent(g)
        if (frameBuffer != null) {
            g?.drawImage(frameBuffer, 0, 0, null)
        }
        repaint()
    }

    fun setFrameBuffer(buffer: BufferedImage) {
        frameBuffer = buffer
    }
}