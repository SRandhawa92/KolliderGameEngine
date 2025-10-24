package com.kollider.engine.core

/**
 * Represents the playable area's bounds. Coordinates are expressed in world space,
 * with [originX] and [originY] describing the top-left corner.
 */
data class WorldBounds(
    val width: Float,
    val height: Float,
    val originX: Float = 0f,
    val originY: Float = 0f,
) {
    val left: Float get() = originX
    val top: Float get() = originY
    val right: Float get() = originX + width
    val bottom: Float get() = originY + height
}
