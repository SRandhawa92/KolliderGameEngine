package com.kollider.engine.ecs.physics

import com.kollider.engine.ecs.Component

/**
 * Component representing an entity's position.
 */
data class Position(var x: Float, var y: Float) : Component()