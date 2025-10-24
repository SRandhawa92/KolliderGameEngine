package com.kollider.engine.ecs.rendering

import com.kollider.engine.ecs.input.InputHandler
import com.kollider.engine.ecs.input.JvmInputHandler
import java.awt.Color
import java.awt.Font
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import javax.swing.JFrame
import javax.swing.SwingUtilities

actual fun createRenderer(canvas: Canvas, inputHandler: InputHandler): Renderer {
    val jvmCanvas = canvas as JvmCanvas
    val jvmInputHandler = inputHandler as JvmInputHandler

    SwingUtilities.invokeLater {
        val frame = JFrame("Kollider Game")
        frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
        frame.setSize(jvmCanvas.canvasWidth, jvmCanvas.canvasHeight)
        frame.contentPane.add(jvmCanvas)
        frame.addKeyListener(jvmInputHandler)
        frame.isVisible = true
    }
    return JvmRenderer(jvmCanvas)
}

class JvmRenderer(private val canvas: JvmCanvas): Renderer {
    private var buffer: BufferedImage = BufferedImage(canvas.canvasWidth, canvas.canvasHeight, BufferedImage.TYPE_INT_ARGB)
    private var g2d: Graphics2D = buffer.createGraphics()

    override fun resize(width: Int, height: Int) {
        buffer = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        g2d = buffer.createGraphics()
    }

    override fun dispose() {
        g2d.dispose()
    }

    override fun drawSprite(spriteAsset: SpriteAsset, width: Float, height: Float,  x: Float, y: Float) {
        val img = spriteAsset.image as? BufferedImage ?: return

        if (!spriteAsset.frames.isNullOrEmpty()) {
            val frames = spriteAsset.frames!!
            val timeMs = System.currentTimeMillis()
            val frameDurationMs = spriteAsset.frameDurationMs ?: 100L
            val frameIndex = ((timeMs / frameDurationMs) % frames.size).toInt()
            val frame = frames[frameIndex] as? BufferedImage ?: return
            g2d.drawImage(frame, x.toInt(), y.toInt(), width.toInt(), height.toInt(), null)
        } else {
            g2d.drawImage(img, x.toInt(), y.toInt(), width.toInt(), height.toInt(), null)
        }
    }

    override fun drawText(text: String, x: Float, y: Float, size: Float, color: Int) {
        g2d.color = Color(color)
        g2d.font = Font("SansSerif", Font.PLAIN, size.toInt())
        g2d.drawString(text, x, y)
    }

    override fun drawRect(x: Float, y: Float, width: Float, height: Float, color: Int) {
        g2d.color = Color(color)
        g2d.fillRect(x.toInt(), y.toInt(), width.toInt(), height.toInt())
    }

    override fun present() {
        canvas.setFrameBuffer(buffer)
        canvas.paint()
    }

    override fun clear() {
        g2d.color = Color.BLACK
        g2d.fillRect(0, 0, canvas.canvasWidth, canvas.canvasHeight)
    }

    override fun drawCircle(x: Float, y: Float, radius: Float, color: Int) {
        g2d.color = Color(color)
        val topLeftX = (x - radius).toInt()
        val topLeftY = (y - radius).toInt()
        val diameter = (radius * 2).toInt()
        g2d.fillOval(topLeftX, topLeftY, diameter, diameter)
    }
}