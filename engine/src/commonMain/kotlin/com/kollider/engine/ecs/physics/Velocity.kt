package com.kollider.engine.ecs.physics

import com.kollider.engine.ecs.Component

/**
 * Component describing the rate of change applied to a [Position] every frame.
 *
 * ```kotlin
 * entity.add(Velocity(vx = 120f, vy = 0f))
 * ```
 */
data class Velocity(var vx: Float, var vy: Float) : Component()
