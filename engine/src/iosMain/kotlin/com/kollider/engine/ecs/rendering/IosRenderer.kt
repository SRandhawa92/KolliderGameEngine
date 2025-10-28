package com.kollider.engine.ecs.rendering

import com.kollider.engine.ecs.input.InputHandler
import com.kollider.engine.ecs.physics.Vector2
import com.kollider.engine.ios.IosPlatform
import com.kollider.engine.ios.IosRenderDelegate
import platform.UIKit.UIImage
import kotlin.time.TimeSource

actual fun createRenderer(canvas: Canvas, inputHandler: InputHandler): Renderer {
    val iosCanvas = canvas as? IosCanvas
        ?: error("IosRenderer requires an IosCanvas but received ${canvas::class.simpleName}.")
    val delegate = IosPlatform.rendererDelegate
        ?: error("IosPlatform.rendererDelegate must be installed before creating the game.")

    return IosRenderer(
        canvas = iosCanvas,
        delegate = delegate,
    )
}

class IosRenderer(
    private val canvas: IosCanvas,
    private val delegate: IosRenderDelegate,
) : Renderer {

    override fun clear() {
        delegate.clear(canvas.canvasWidth, canvas.canvasHeight)
    }

    override fun drawSprite(
        spriteAsset: SpriteAsset,
        width: Float,
        height: Float,
        x: Float,
        y: Float,
    ) {
        val image = spriteAsset.resolveCurrentFrame() ?: return
        delegate.drawSprite(image, width, height, x, y)
    }

    override fun present() {
        delegate.present()
    }

    override fun resize(width: Int, height: Int) {
        canvas.updateSize(width, height)
        delegate.resize(width, height)
    }

    override fun dispose() {
        delegate.dispose()
    }

    override fun drawText(text: String, x: Float, y: Float, size: Float, color: Int) {
        delegate.drawText(text, x, y, size, color)
    }

    override fun drawRect(x: Float, y: Float, width: Float, height: Float, color: Int) {
        delegate.drawRect(x, y, width, height, color)
    }

    override fun drawCircle(x: Float, y: Float, radius: Float, color: Int) {
        delegate.drawCircle(x, y, radius, color)
    }

    override fun drawLine(
        x1: Float,
        y1: Float,
        x2: Float,
        y2: Float,
        thickness: Float,
        color: Int,
    ) {
        delegate.drawLine(x1, y1, x2, y2, thickness, color)
    }

    override fun drawPolygon(points: List<Vector2>, color: Int) {
        delegate.drawPolygon(points, color)
    }
}

private fun SpriteAsset.resolveCurrentFrame(): UIImage? {
    val baseImage = image as? UIImage
    val availableFrames = frames

    if (!availableFrames.isNullOrEmpty()) {
        val durationMs = frameDurationMs ?: 100L
        val frameIndex = ((TimeSource.Monotonic.markNow().elapsedNow().inWholeMilliseconds / durationMs) % availableFrames.size).toInt()
        val frameImage = availableFrames[frameIndex] as? UIImage
        if (frameImage != null) {
            return frameImage
        }
    }

    return baseImage
}
