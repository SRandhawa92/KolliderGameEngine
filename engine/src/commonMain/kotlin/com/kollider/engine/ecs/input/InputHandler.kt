package com.kollider.engine.ecs.input

import com.kollider.engine.core.GameConfig
import com.kollider.engine.ecs.physics.Vector2

/**
 * Creates a platform-specific [InputHandler] bound to the provided [GameConfig].
 */
expect fun createInputHandler(config: GameConfig): InputHandler

/**
 * Bridges platform input APIs with the engine's action/movement model.
 */
interface InputHandler {
    /**
     * Event dispatcher used by the [InputSystem] to deliver discrete actions.
     *
     * ```kotlin
     * inputHandler.dispatcher.emit(Shoot, true)
     * ```
     */
    val dispatcher: InputDispatcher

    /**
     * Reports whether [action] is currently active (e.g., key held down).
     *
     * ```kotlin
     * if (inputHandler.isActionActive(Shoot)) fire()
     * ```
     */
    fun isActionActive(action: Action): Boolean

    /**
     * Returns the current analog movement vector, typically normalised.
     *
     * ```kotlin
     * val movement = inputHandler.getMovement()
     * ```
     */
    fun getMovement(): Vector2
}
