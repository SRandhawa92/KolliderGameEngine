package com.kollider.engine.ecs.rendering

import android.graphics.Paint
import android.graphics.Rect
import com.kollider.engine.ecs.input.AndroidInputHandler
import com.kollider.engine.ecs.input.InputHandler

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
    private var buffer: android.graphics.Bitmap =
        android.graphics.Bitmap.createBitmap(
            canvas.canvasWidth,
            canvas.canvasHeight,
            android.graphics.Bitmap.Config.ARGB_8888
        )

    private var graphics = android.graphics.Canvas(buffer)

    override fun clear() {
        graphics.drawColor(android.graphics.Color.BLACK)
    }

    override fun drawSprite(spriteAsset: SpriteAsset, x: Float, y: Float) {
        // Draw the spriteAsset on Android
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

    override fun present() {
        canvas.setFrameBuffer(buffer)
        canvas.paint()
    }

    override fun resize(width: Int, height: Int) {
        buffer = android.graphics.Bitmap.createBitmap(width, height, android.graphics.Bitmap.Config.ARGB_8888)
        graphics = android.graphics.Canvas(buffer)
    }

    override fun dispose() {
        buffer.recycle()
    }
}