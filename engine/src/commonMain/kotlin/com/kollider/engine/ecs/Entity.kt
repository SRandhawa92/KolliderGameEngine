package com.kollider.engine.ecs

import kotlin.reflect.KClass

/**
 * A game entity that can have multiple components.
 *
 * @property id a unique identifier for the entity.
 */
class Entity(val id: Int) {
    private val components: MutableMap<KClass<*>, Component> = mutableMapOf()

    /**
     * Attaches a component to this entity.
     */
    fun <T : Component> add(component: T): Entity {
        components[component::class] = component
        return this
    }

    /**
     * Retrieves a component from this entity.
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Component> get(componentClass: KClass<T>): T? {
        return components.entries.firstOrNull { componentClass.isInstance(it.value) }?.value as? T
    }

    /**
     * Removes a component from this entity.
     */
    fun <T : Component> remove(componentClass: KClass<T>) {
        components.remove(componentClass)
    }

    /**
     * Checks if the entity has a specific component.
     */
    fun has(componentClass: KClass<out Component>): Boolean {
        return components.containsKey(componentClass)
    }
}
