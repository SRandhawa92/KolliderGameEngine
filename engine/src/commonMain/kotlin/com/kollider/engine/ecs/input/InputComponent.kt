package com.kollider.engine.ecs.input

import com.kollider.engine.ecs.Component
import com.kollider.engine.ecs.physics.Vector2

/**
 * Component that stores per-entity input state and optional listeners.
 *
 * Use [bindAction] to opt into discrete actions, and [movementEnabled] to receive the shared
 * analog [movement] vector.
 *
 * ```kotlin
 * entity.add(InputComponent().apply {
 *     bindAction(Shoot)
 *     onAction(Shoot) { fireProjectile() }
 * })
 * ```
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

    /**
     * Registers an action that the component is willing to receive.
     *
     * ```kotlin
     * input.bindAction(Pause)
     * ```
     */
    fun bindAction(action: Action) {
        actionBindings.add(action)
    }

    /**
     * Stops forwarding events for the provided [action].
     *
     * ```kotlin
     * input.unbindAction(Shoot)
     * ```
     */
    fun unbindAction(action: Action) {
        actionBindings.remove(action)
    }

    /**
     * Adds a listener for a specific [action] and automatically binds it.
     *
     * ```kotlin
     * input.onAction(Shoot) { event -> fireWeapon(event.isActive) }
     * ```
     */
    fun onAction(action: Action, listener: InputListener) {
        actionListeners.getOrPut(action) { mutableListOf() }.add(listener)
        bindAction(action)
    }

    /**
     * Adds a listener that receives every dispatched [InputEvent].
     *
     * ```kotlin
     * input.onAny { event -> log(event) }
     * ```
     */
    fun onAny(listener: InputListener) {
        anyListeners.add(listener)
    }

    /**
     * Returns `true` if the component listens for the supplied [action].
     */
    internal fun accepts(action: Action): Boolean = actionBindings.contains(action)

    /**
     * Delivers [event] to registered listeners.
     */
    internal fun dispatch(event: InputEvent) {
        anyListeners.forEach { it.onInput(event) }
        actionListeners[event.action]?.forEach { it.onInput(event) }
    }
}
