package com.kollider.engine.ecs.input

import com.kollider.engine.ecs.Component
import com.kollider.engine.ecs.physics.Vector2

/**
 * Component that holds input state and optional per-action listeners.
 */
class InputComponent(
    var movement: Vector2 = Vector2(0f, 0f),
    var shoot: Boolean = false,
    var paused: Boolean = false,
) : Component() {
    private val actionListeners = mutableMapOf<Action, MutableList<InputListener>>()
    private val anyListeners = mutableListOf<InputListener>()

    fun onAction(action: Action, listener: InputListener) {
        actionListeners.getOrPut(action) { mutableListOf() }.add(listener)
    }

    fun onAny(listener: InputListener) {
        anyListeners.add(listener)
    }

    internal fun dispatch(event: InputEvent) {
        anyListeners.forEach { it.onInput(event) }
        actionListeners[event.action]?.forEach { it.onInput(event) }
    }
}
