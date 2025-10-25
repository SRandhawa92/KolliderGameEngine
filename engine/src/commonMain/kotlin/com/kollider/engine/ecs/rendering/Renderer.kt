package com.kollider.engine.ecs.rendering

import com.kollider.engine.ecs.input.InputHandler

/**
 * Creates a platform renderer bound to the [canvas] and [inputHandler].
 *
 * ```kotlin
 * val renderer = createRenderer(canvas, inputHandler)
 * world.addSystem(RenderSystem(renderer, config))
 * ```
 */
expect fun createRenderer(canvas: Canvas, inputHandler: InputHandler): Renderer

/**
 * Cross-platform drawing contract used by the [RenderSystem].
 */
interface Renderer {
    /**
     * Clears the frame with a background color.
     *
     * ```kotlin
     * renderer.clear()
     * ```
     */
    fun clear()

    /**
     * Draws the provided [spriteAsset] using the supplied dimensions and coordinates.
     *
     * ```kotlin
     * renderer.drawSprite(playerSprite, width = 48f, height = 48f, x = 100f, y = 100f)
     * ```
     */
    fun drawSprite(spriteAsset: SpriteAsset, width: Float, height: Float, x: Float, y: Float)

    /**
     * Draws text at the specified position with the given size and ARGB color.
     *
     * ```kotlin
     * renderer.drawText("Score: 42", x = 16f, y = 24f, size = 18f, color = 0xFFFFFFFF.toInt())
     * ```
     */
    fun drawText(text: String, x: Float, y: Float, size: Float, color: Int)

    /**
     * Draws a filled rectangle.
     *
     * ```kotlin
     * renderer.drawRect(0f, 0f, 32f, 32f, 0xFFFF0000.toInt())
     * ```
     */
    fun drawRect(x: Float, y: Float, width: Float, height: Float, color: Int)

    /**
     * Draws a filled circle.
     *
     * ```kotlin
     * renderer.drawCircle(16f, 16f, 8f, 0xFFFFFFFF.toInt())
     * ```
     */
    fun drawCircle(x: Float, y: Float, radius: Float, color: Int)

    /**
     * Presents the drawn frame to the screen.
     *
     * ```kotlin
     * renderer.present()
     * ```
     */
    fun present()

    /**
     * Called when the rendering surface is resized.
     *
     * ```kotlin
     * renderer.resize(width, height)
     * ```
     */
    fun resize(width: Int, height: Int)

    /**
     * Releases any resources used by the renderer.
     *
     * ```kotlin
     * renderer.dispose()
     * ```
     */
    fun dispose()
}
