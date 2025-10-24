package com.kollider.engine.ecs.rendering

import com.kollider.engine.ecs.Component

/**
 * Represents something that can be drawn.
 */
sealed class Drawable: Component() {
    data class Sprite(val spriteAsset: SpriteAsset, val width: Float, val height: Float, val offsetX: Float = 0f, val offsetY: Float = 0f) : Drawable()
    data class Rect(val width: Float, val height: Float, val color: Int, val offsetX: Float = 0f, val offsetY: Float = 0f) : Drawable()
    data class Circle(val radius: Float, val color: Int, val offsetX: Float = 0f, val offsetY: Float = 0f) : Drawable()
    data class Text(val text: String, val size: Float, val color: Int, val offsetX: Float = 0f, val offsetY: Float = 0f) : Drawable()
}