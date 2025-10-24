package com.kollider.engine.ecs

/**
 * Retrieves the component of type [C] or throws an informative error if missing.
 *
 * ```kotlin
 * val velocity = entity.require<Velocity>()
 * velocity.vx += 10f
 * ```
 */
inline fun <reified C : Component> Entity.require(): C =
    get(C::class) ?: error("Entity $id is missing ${C::class.simpleName}")
