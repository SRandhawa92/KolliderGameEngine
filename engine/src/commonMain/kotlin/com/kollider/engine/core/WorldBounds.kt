package com.kollider.engine.core

/**
 * Represents the playable area's bounds. Coordinates are expressed in world space,
 * with [originX] and [originY] describing the top-left corner.
 *
 * ```kotlin
 * val bounds = WorldBounds(width = 800f, height = 600f)
 * if (player.position.x > bounds.right) wrapAround()
 * ```
 */
data class WorldBounds(
    val width: Float,
    val height: Float,
    val originX: Float = 0f,
    val originY: Float = 0f,
) {
    /** Left edge of the playable area. */
    val left: Float get() = originX

    /** Top edge of the playable area. */
    val top: Float get() = originY

    /** Right edge of the playable area. */
    val right: Float get() = originX + width

    /** Bottom edge of the playable area. */
    val bottom: Float get() = originY + height
}
