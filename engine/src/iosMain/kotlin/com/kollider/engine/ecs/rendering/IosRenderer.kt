package com.kollider.engine.ecs.rendering

import com.kollider.engine.ecs.input.InputHandler

actual fun createRenderer(canvas: Canvas, inputHandler: InputHandler): Renderer {
    return IosRenderer()
}

class IosRenderer: Renderer {

    override fun clear() {
        // Clear the frame on iOS
    }

    override fun present() {
        // Present the frame on iOS
    }

    override fun resize(width: Int, height: Int) {
        // Resize the rendering surface on iOS
    }

    override fun dispose() {
        // Dispose of any resources used by the renderer on iOS
    }

    override fun drawSprite(spriteAsset: SpriteAsset, x: Float, y: Float) {
        // Draw the spriteAsset on iOS
    }

    override fun drawText(text: String, x: Float, y: Float, size: Float, color: Int) {
        // Draw the text on iOS
    }

    override fun drawRect(x: Float, y: Float, width: Float, height: Float, color: Int) {
        // Draw the rectangle on iOS
    }

    override fun drawCircle(x: Float, y: Float, radius: Float, color: Int) {
        // Draw the circle on iOS
    }
}