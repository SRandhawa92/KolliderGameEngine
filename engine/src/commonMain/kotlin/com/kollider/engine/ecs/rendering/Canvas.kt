package com.kollider.engine.ecs.rendering

import com.kollider.engine.core.GameConfig

/**
 * Creates a platform-specific drawing surface that the [Renderer] can target.
 *
 * ```kotlin
 * val canvas = createCanvas(config)
 * val renderer = createRenderer(canvas, inputHandler)
 * ```
 */
expect fun createCanvas(config: GameConfig): Canvas

/**
 * Abstraction over a render surface that can present frames to the user.
 */
interface Canvas {
    /**
     * Current width of the canvas in pixels.
     */
    val canvasWidth: Int

    /**
     * Current height of the canvas in pixels.
     */
    val canvasHeight: Int

    /**
     * Triggers a paint pass. Platform implementations typically enqueue a redraw.
     *
     * ```kotlin
     * canvas.paint()
     * ```
     */
    fun paint()
}
