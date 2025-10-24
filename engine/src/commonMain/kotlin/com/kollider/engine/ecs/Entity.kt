package com.kollider.engine.ecs

import kotlin.reflect.KClass

/**
 * A game entity that can have multiple components.
 *
 * @property id a unique identifier for the entity.
 */
class Entity(val id: Int) {
    private val components: MutableMap<KClass<*>, Component> = mutableMapOf()
    private var observer: ComponentObserver? = null

    /**
     * Attaches a component to this entity.
     */
    fun <T : Component> add(component: T): Entity {
        val type = component::class
        components[type] = component
        observer?.onComponentAdded(this, type)
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
        val removed = components.remove(componentClass)
        if (removed != null) {
            observer?.onComponentRemoved(this, componentClass)
        }
    }

    /**
     * Checks if the entity has a specific component.
     */
    fun has(componentClass: KClass<out Component>): Boolean {
        return components.containsKey(componentClass)
    }

    internal fun bindObserver(observer: ComponentObserver) {
        this.observer = observer
    }

    internal interface ComponentObserver {
        fun onComponentAdded(entity: Entity, type: KClass<out Component>)
        fun onComponentRemoved(entity: Entity, type: KClass<out Component>)
    }
}
