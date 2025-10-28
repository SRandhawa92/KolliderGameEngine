package com.kollider.engine.ecs.input

/**
 * Central registry mapping action names to their instances.
 *
 * Platforms like iOS that bridge input from UIKit/SwiftUI use this registry
 * to resolve actions based on their string identifiers.
 */
object ActionRegistry {
    private val actions = mutableMapOf<String, Action>()

    /**
     * Registers an [action] so that platform bridges can resolve it by name.
     *
     * ```kotlin
     * ActionRegistry.register(Shoot)
     * ```
     */
    fun register(action: Action) {
        actions[action.name] = action
    }

    /**
     * Resolves an action by its [name], returning null when the action
     * has not been registered yet.
     */
    fun resolve(name: String): Action? = actions[name]

    /**
     * Clears all registered actions. Primarily intended for tests.
     */
    fun reset() {
        actions.clear()
    }
}
