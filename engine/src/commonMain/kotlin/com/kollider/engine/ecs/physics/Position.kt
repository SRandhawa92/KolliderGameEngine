package com.kollider.engine.ecs.physics

import com.kollider.engine.ecs.Component

/**
 * Component storing an entity's world-space coordinates.
 *
 * ```kotlin
 * entity.add(Position(x = 64f, y = 128f))
 * ```
 */
data class Position(var x: Float, var y: Float) : Component()
