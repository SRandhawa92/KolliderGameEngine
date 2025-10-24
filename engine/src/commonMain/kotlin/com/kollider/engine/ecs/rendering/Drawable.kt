package com.kollider.engine.ecs.rendering

import com.kollider.engine.ecs.Component

/**
 * Marker component for entities that can be drawn by the [RenderSystem].
 *
 * Choose the subclass that best represents the visual you want to render.
 *
 * ```kotlin
 * entity.add(
 *     Drawable.Sprite(
 *         spriteAsset = textures.player,
 *         width = 64f,
 *         height = 64f
 *     )
 * )
 * ```
 */
sealed class Drawable: Component() {
    /**
     * Draws a texture-backed sprite.
     *
     * ```kotlin
     * entity.add(Drawable.Sprite(spriteAsset, width = 32f, height = 32f))
     * ```
     */
    data class Sprite(
        val spriteAsset: SpriteAsset,
        val width: Float,
        val height: Float,
        val offsetX: Float = 0f,
        val offsetY: Float = 0f
    ) : Drawable()

    /**
     * Draws a solid rectangle using an ARGB [color].
     *
     * ```kotlin
     * entity.add(Drawable.Rect(16f, 8f, 0xFF00FF00.toInt()))
     * ```
     */
    data class Rect(
        val width: Float,
        val height: Float,
        val color: Int,
        val offsetX: Float = 0f,
        val offsetY: Float = 0f
    ) : Drawable() {
        val left: Float get() = offsetX
        val top: Float get() = offsetY
        val right: Float get() = offsetX + width
        val bottom: Float get() = offsetY + height
    }

    /**
     * Draws a filled circle.
     *
     * ```kotlin
     * entity.add(Drawable.Circle(radius = 12f, color = 0xFFFFFFFF.toInt()))
     * ```
     */
    data class Circle(
        val radius: Float,
        val color: Int,
        val offsetX: Float = 0f,
        val offsetY: Float = 0f
    ) : Drawable()

    /**
     * Renders text using the platform renderer's default font support.
     *
     * ```kotlin
     * entity.add(Drawable.Text("Play", size = 24f, color = 0xFFFFFFFF.toInt()))
     * ```
     */
    data class Text(
        val text: String,
        val size: Float,
        val color: Int,
        val offsetX: Float = 0f,
        val offsetY: Float = 0f
    ) : Drawable()

    /**
     * Groups multiple drawables so they render as a single logical unit.
     *
     * Child offsets are relative to this composite's offset.
     */
    data class Composite(
        val elements: List<Drawable>,
        val offsetX: Float = 0f,
        val offsetY: Float = 0f,
    ) : Drawable()
}
