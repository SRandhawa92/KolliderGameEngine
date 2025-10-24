package com.kollider.engine.ecs.input

import com.kollider.engine.ecs.Component
import com.kollider.engine.ecs.physics.Vector2

/**
 * Component that holds input state and optional per-action listeners.
 * Games can restrict which actions this component reacts to and whether it should
 * receive shared analog input (movement) from the active input handler.
 */
class InputComponent(
    var movement: Vector2 = Vector2(0f, 0f),
    var shoot: Boolean = false,
    var paused: Boolean = false,
    var movementEnabled: Boolean = true,
) : Component() {
    internal var ownerEntityId: Int = -1

    private val actionListeners = mutableMapOf<Action, MutableList<InputListener>>()
    private val anyListeners = mutableListOf<InputListener>()
    private val actionBindings = mutableSetOf<Action>()

    fun bindAction(action: Action) {
        actionBindings.add(action)
    }

    fun unbindAction(action: Action) {
        actionBindings.remove(action)
    }

    fun onAction(action: Action, listener: InputListener) {
        actionListeners.getOrPut(action) { mutableListOf() }.add(listener)
        bindAction(action)
    }

    fun onAny(listener: InputListener) {
        anyListeners.add(listener)
    }

    internal fun accepts(action: Action): Boolean = actionBindings.contains(action)

    internal fun dispatch(event: InputEvent) {
        anyListeners.forEach { it.onInput(event) }
        actionListeners[event.action]?.forEach { it.onInput(event) }
    }
}
