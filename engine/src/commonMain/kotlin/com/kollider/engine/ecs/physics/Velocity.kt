package com.kollider.engine.ecs.physics

import com.kollider.engine.ecs.Component

/**
 * Component representing an entity's velocity.
 */
data class Velocity(var vx: Float, var vy: Float) : Component()