package com.kollider.engine.ecs.rendering

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.kollider.engine.core.GameConfig
import com.kollider.engine.ecs.input.AndroidInputHandler

actual fun createCanvas(config: GameConfig): Canvas {
    return AndroidCanvas(
        context = config.appContext.get()!!,
        canvasHeight = config.height,
        canvasWidth = config.width
    )
}

@SuppressLint("ViewConstructor")
class AndroidCanvas(
    context: Context,
    attrs: AttributeSet? = null,
    override val canvasHeight: Int,
    override val canvasWidth: Int
): View(context, attrs), Canvas {

    private lateinit var inputHandler: AndroidInputHandler

    private var frameBuffer: android.graphics.Bitmap? = null


    fun setInputHandler(handler: AndroidInputHandler) {
        inputHandler = handler
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                // The user’s finger has come off the screen.
                // Perform any cleanup or action deactivation here.
                inputHandler.clearActions()
            }
        }
        // Pass the event to the GestureDetector to handle flings, taps, etc.
        inputHandler.onTouchEvent(event)
        return true
    }

    override fun paint() {
        invalidate()
    }

    override fun onDraw(canvas: android.graphics.Canvas) {
        if (frameBuffer != null) {
            canvas.drawBitmap(frameBuffer!!, 0f, 0f, null)
        }
        super.onDraw(canvas)
    }

    fun setFrameBuffer(buffer: android.graphics.Bitmap) {
        frameBuffer = buffer
    }
}