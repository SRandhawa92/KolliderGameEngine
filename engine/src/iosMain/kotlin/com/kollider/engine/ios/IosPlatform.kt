package com.kollider.engine.ios

import com.kollider.engine.ecs.input.IosInputHandler
import com.kollider.engine.ecs.physics.Vector2
import kotlin.native.concurrent.ThreadLocal
import platform.UIKit.UIImage

/**
 * Represents the size of the iOS drawing surface in physical pixels.
 */
data class IosCanvasSize(val width: Int, val height: Int)

/**
 * Host-side contract for UIKit/SwiftUI views that present engine frames.
 */
interface IosCanvasHost {
    fun currentSize(): IosCanvasSize
    fun requestRender()
    fun onResize(width: Int, height: Int)
}

/**
 * Delegate implemented in Swift/Objective-C that executes the actual Metal/CoreGraphics drawing.
 */
interface IosRenderDelegate {
    fun clear(width: Int, height: Int)
    fun drawSprite(image: UIImage, width: Float, height: Float, x: Float, y: Float)
    fun drawText(text: String, x: Float, y: Float, size: Float, color: Int)
    fun drawRect(x: Float, y: Float, width: Float, height: Float, color: Int)
    fun drawCircle(x: Float, y: Float, radius: Float, color: Int)
    fun drawLine(x1: Float, y1: Float, x2: Float, y2: Float, thickness: Float, color: Int)
    fun drawPolygon(points: List<Vector2>, color: Int)
    fun present()
    fun resize(width: Int, height: Int)
    fun dispose()
}

/**
 * Global registry where the host app installs its canvas and renderer implementations.
 */
@ThreadLocal
object IosPlatform {
    var canvasHost: IosCanvasHost? = null
    var rendererDelegate: IosRenderDelegate? = null
}

/**
 * Helper used by Swift/Objective-C code to forward input events into the engine.
 */
@ThreadLocal
object IosInputBridge {
    private var handler: IosInputHandler? = null

    internal fun attach(handler: IosInputHandler) {
        this.handler = handler
    }

    internal fun detach(handler: IosInputHandler) {
        if (this.handler === handler) {
            this.handler = null
        }
    }

    fun setMovement(x: Float, y: Float) {
        handler?.setMovement(x, y)
    }

    fun setActionState(actionName: String, isActive: Boolean, targetEntityId: Int? = null) {
        handler?.setActionState(actionName, isActive, targetEntityId)
    }

    fun clearAction(actionName: String) {
        handler?.clearAction(actionName)
    }
}
