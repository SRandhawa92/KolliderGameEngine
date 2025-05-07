package com.kollider.engine.ecs.rendering

import com.kollider.engine.ecs.input.InputHandler

expect fun createRenderer(canvas: Canvas, inputHandler: InputHandler): Renderer

interface Renderer {
    /** Clears the frame with a background color. */
    fun clear()

    /** Draws a spriteAsset at the given coordinates. */
    fun drawSprite(spriteAsset: SpriteAsset, x: Float, y: Float)

    /** Draws text at the specified position with the given size and color. */
    fun drawText(text: String, x: Float, y: Float, size: Float, color: Int)

    /** Draws a rectangle with the specified dimensions and color. */
    fun drawRect(x: Float, y: Float, width: Float, height: Float, color: Int)

    /** Draws a circle with the specified radius and color. */
    fun drawCircle(x: Float, y: Float, radius: Float, color: Int)

    /** Presents the drawn frame to the screen. */
    fun present()

    /** Called when the rendering surface is resized. */
    fun resize(width: Int, height: Int)

    /** Releases any resources used by the renderer. */
    fun dispose()
}

