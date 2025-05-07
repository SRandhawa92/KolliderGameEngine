package com.kollider.engine.ecs.input

/**
 * A simple interface representing a discrete action.
 */
interface Action {
    val name: String
}

/**
 * A minimal set of built-in actions.
 * Developers can define additional actions as needed.
 */
object Shoot : Action { override val name = "Shoot" }
object Pause : Action { override val name = "Pause" }