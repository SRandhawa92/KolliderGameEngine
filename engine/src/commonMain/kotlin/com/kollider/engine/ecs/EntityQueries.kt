package com.kollider.engine.ecs

import kotlin.reflect.KClass

/**
 * Returns a [Sequence] of entities that have all of the requested [componentTypes].
 * Useful in systems to avoid repeated `filter { has(...) }` passes over the world.
 */
fun Iterable<Entity>.withAll(vararg componentTypes: KClass<out Component>): Sequence<Entity> =
    asSequence().filter { entity -> componentTypes.all(entity::has) }

/**
 * Retrieves the component of type [C] or throws an error with a helpful message.
 * Handy when the presence of the component is already guaranteed by a prior query.
 */
inline fun <reified C : Component> Entity.require(): C =
    get(C::class) ?: error("Entity $id is missing ${C::class.simpleName}")
