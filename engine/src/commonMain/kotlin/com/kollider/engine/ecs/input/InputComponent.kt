package com.kollider.engine.ecs.input

import com.kollider.engine.ecs.Component
import com.kollider.engine.ecs.physics.Vector2

/**
 * Component that holds input state.
 */
data class InputComponent(
    var movement: Vector2 = Vector2(0f, 0f),
    var shoot: Boolean = false,
    var paused: Boolean = false
) : Component()