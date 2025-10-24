package com.kollider.engine.ecs.rendering

import com.kollider.engine.core.GameConfig
import java.awt.Dimension
import java.awt.Graphics
import java.awt.image.BufferedImage
import javax.swing.JPanel
import javax.swing.SwingUtilities

actual fun createCanvas(config: GameConfig): Canvas {
    return JvmCanvas(config.width, config.height)
}

class JvmCanvas(
    override val canvasWidth: Int,
    override val canvasHeight: Int
) : JPanel(), Canvas {

    /** The latest rendered frame. */
    private var frameBuffer: BufferedImage? = null

    init {
        preferredSize = Dimension(canvasWidth, canvasHeight)
        isDoubleBuffered = true
    }

    override fun paint() {
        if (SwingUtilities.isEventDispatchThread()) {
            repaint()
        } else {
            SwingUtilities.invokeLater { repaint() }
        }
    }

    override fun paintComponent(g: Graphics?) {
        super.paintComponent(g)
        val buffer = frameBuffer ?: return
        g?.drawImage(buffer, 0, 0, null)
    }

    fun presentFrame(buffer: BufferedImage) {
        frameBuffer = buffer
        repaint()
    }
}
