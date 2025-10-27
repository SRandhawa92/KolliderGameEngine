package com.kollider.engine.ecs.rendering

import android.graphics.Bitmap.createBitmap
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import com.kollider.engine.ecs.input.AndroidInputHandler
import com.kollider.engine.ecs.input.InputHandler
import com.kollider.engine.ecs.physics.Vector2

actual fun createRenderer(canvas: Canvas, inputHandler: InputHandler): Renderer {
    canvas as AndroidCanvas
    inputHandler as AndroidInputHandler
    canvas.setInputHandler(inputHandler)
    return AndroidRenderer(canvas)
}
class AndroidRenderer(private val canvas: AndroidCanvas): Renderer {
    init {
        val activity = canvas.context as android.app.Activity
        activity.setContentView(canvas)

    }
    private var buffer = createBitmap(canvas.canvasWidth, canvas.canvasHeight, android.graphics.Bitmap.Config.ARGB_8888)

    private var graphics = android.graphics.Canvas(buffer)

    override fun clear() {
        graphics.drawColor(android.graphics.Color.BLACK)
    }

    override fun drawSprite(spriteAsset: SpriteAsset, width: Float, height: Float, x: Float, y: Float) {
        val bitmap = spriteAsset.image as? android.graphics.Bitmap ?: return

        if (!spriteAsset.frames.isNullOrEmpty()) {
            val frames = spriteAsset.frames!!
            val timeMs = System.currentTimeMillis()
            val frameDurationMs = spriteAsset.frameDurationMs ?: 100L
            val frameIndex = ((timeMs / frameDurationMs) % frames.size).toInt()
            val frame = frames[frameIndex] as? android.graphics.Bitmap ?: return
            graphics.drawBitmap(frame, null, Rect(x.toInt(), y.toInt(), (x + width).toInt(), (y + height).toInt()), null)
        } else {
            graphics.drawBitmap(bitmap, null, Rect(x.toInt(), y.toInt(), (x + width).toInt(), (y + height).toInt()), null)
        }
    }

    override fun drawText(text: String, x: Float, y: Float, size: Float, color: Int) {
        val paint = Paint().apply {
            this.color = color
            this.textSize = size
        }
        graphics.drawText(text, x, y, paint)
    }

    override fun drawRect(x: Float, y: Float, width: Float, height: Float, color: Int) {
        val rect = Rect(x.toInt(), y.toInt(), (x + width).toInt(), (y + height).toInt())
        val paint = Paint().apply { this.color = color }
        graphics.drawRect(rect, paint)
    }

    override fun drawCircle(x: Float, y: Float, radius: Float, color: Int) {
        val paint = Paint().apply { this.color = color }
        graphics.drawCircle(x, y, radius, paint)
    }

    override fun drawLine(x1: Float, y1: Float, x2: Float, y2: Float, thickness: Float, color: Int) {
        val paint = Paint().apply {
            this.color = color
            strokeWidth = thickness
            style = Paint.Style.STROKE
        }
        graphics.drawLine(x1, y1, x2, y2, paint)
    }

    override fun drawPolygon(points: List<Vector2>, color: Int) {
        if (points.size < 3) return
        val paint = Paint().apply {
            this.color = color
            style = Paint.Style.FILL
        }
        val path = Path()
        val first = points.first()
        path.moveTo(first.x, first.y)
        for (i in 1 until points.size) {
            val point = points[i]
            path.lineTo(point.x, point.y)
        }
        path.close()
        graphics.drawPath(path, paint)
    }

    override fun present() {
        canvas.setFrameBuffer(buffer)
        canvas.paint()
    }

    override fun resize(width: Int, height: Int) {
        buffer = createBitmap(width, height, android.graphics.Bitmap.Config.ARGB_8888)
        graphics = android.graphics.Canvas(buffer)
    }

    override fun dispose() {
        buffer.recycle()
    }
}
