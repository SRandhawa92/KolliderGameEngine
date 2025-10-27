package com.kollider.engine.ecs.rendering

import com.kollider.engine.ecs.input.InputHandler
import com.kollider.engine.ecs.physics.Vector2

actual fun createRenderer(canvas: Canvas, inputHandler: InputHandler): Renderer {
    return JsRenderer()
}

class JsRenderer: Renderer {

    override fun clear() {
        // Clear the frame on JS
    }

    override fun drawSprite(
        spriteAsset: SpriteAsset,
        width: Float,
        height: Float,
        x: Float,
        y: Float
    ) {
        // Draw the spriteAsset on JS
    }

    override fun drawText(text: String, x: Float, y: Float, size: Float, color: Int) {
        // Draw the text on JS
    }

    override fun drawRect(x: Float, y: Float, width: Float, height: Float, color: Int) {
        // Draw the rectangle on JS
    }

    override fun present() {
        // Present the frame on JS
    }

    override fun resize(width: Int, height: Int) {
        // Resize the rendering surface on JS
    }

    override fun dispose() {
        // Dispose of any resources used by the renderer on JS
    }

    override fun drawCircle(x: Float, y: Float, radius: Float, color: Int) {
        // Draw the circle on JS
    }

    override fun drawLine(x1: Float, y1: Float, x2: Float, y2: Float, thickness: Float, color: Int) {
        // Draw the line on JS
    }

    override fun drawPolygon(points: List<Vector2>, color: Int) {
        // Draw the polygon on JS
    }
}
