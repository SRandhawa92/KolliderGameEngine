package com.kollider.engine.ecs.rendering

import com.kollider.engine.ecs.input.InputHandler
import com.kollider.engine.ecs.input.JvmInputHandler
import com.kollider.engine.ecs.physics.Vector2
import java.awt.BasicStroke
import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import java.awt.Graphics2D
import java.awt.geom.Line2D
import java.awt.geom.Path2D
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
        updateCanvasSize(width, height)
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
        g2d.color = Color(color, true)
        g2d.font = Font("SansSerif", Font.PLAIN, size.toInt())
        g2d.drawString(text, x, y)
    }

    override fun drawRect(x: Float, y: Float, width: Float, height: Float, color: Int) {
        g2d.color = Color(color, true)
        g2d.fillRect(x.toInt(), y.toInt(), width.toInt(), height.toInt())
    }

    override fun present() {
        if (SwingUtilities.isEventDispatchThread()) {
            canvas.presentFrame(buffer)
        } else {
            SwingUtilities.invokeLater { canvas.presentFrame(buffer) }
        }
    }

    override fun clear() {
        g2d.color = Color.BLACK
        g2d.fillRect(0, 0, buffer.width, buffer.height)
    }

    override fun drawCircle(x: Float, y: Float, radius: Float, color: Int) {
        g2d.color = Color(color, true)
        val topLeftX = (x - radius).toInt()
        val topLeftY = (y - radius).toInt()
        val diameter = (radius * 2).toInt()
        g2d.fillOval(topLeftX, topLeftY, diameter, diameter)
    }

    override fun drawLine(x1: Float, y1: Float, x2: Float, y2: Float, thickness: Float, color: Int) {
        val previousStroke = g2d.stroke
        g2d.color = Color(color, true)
        g2d.stroke = BasicStroke(thickness)
        g2d.draw(Line2D.Float(x1, y1, x2, y2))
        g2d.stroke = previousStroke
    }

    override fun drawPolygon(points: List<Vector2>, color: Int) {
        if (points.size < 3) return
        val path = Path2D.Float()
        val first = points.first()
        path.moveTo(first.x, first.y)
        for (i in 1 until points.size) {
            val point = points[i]
            path.lineTo(point.x, point.y)
        }
        path.closePath()
        g2d.color = Color(color, true)
        g2d.fill(path)
    }

    private fun updateCanvasSize(width: Int, height: Int) {
        if (SwingUtilities.isEventDispatchThread()) {
            canvas.preferredSize = Dimension(width, height)
            canvas.revalidate()
        } else {
            SwingUtilities.invokeLater {
                canvas.preferredSize = Dimension(width, height)
                canvas.revalidate()
            }
        }
    }
}
