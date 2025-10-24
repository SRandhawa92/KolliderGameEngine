package com.kollider.engine.ecs.input

/**
 * Represents a discrete, nameable input action (e.g., "Jump" or "Pause").
 *
 * Implementations can be simple objects or enums.
 */
interface Action {
    val name: String
}

/**
 * Built-in actions used by the sample games. Create your own to extend the input map.
 */
object Shoot : Action { override val name = "Shoot" }
object Pause : Action { override val name = "Pause" }
