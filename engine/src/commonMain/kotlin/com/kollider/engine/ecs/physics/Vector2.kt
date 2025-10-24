package com.kollider.engine.ecs.physics

/**
 * Mutable 2D vector used for analog movement or directional input.
 *
 * ```kotlin
 * val movement = Vector2()
 * movement.x = input.getHorizontal()
 * movement.y = input.getVertical()
 * ```
 */
data class Vector2(var x: Float = 0f, var y: Float = 0f)
