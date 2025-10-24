package com.kollider.engine.ecs

import com.kollider.engine.ecs.input.InputComponent
import kotlin.reflect.KClass

/**
 * Represents an addressable object in the ECS world.
 *
 * Entities are composed of [Component] instances and contain no behaviour on their own.
 *
 * ```kotlin
 * val player = world.createEntity()
 * player.add(Position(10f, 20f))
 * player.add(Velocity(0f, 0f))
 * ```
 *
 * @property id Unique identifier assigned by the [World].
 */
class Entity(val id: Int) {
    private val components: MutableMap<KClass<*>, Component> = mutableMapOf()
    private var observer: ComponentObserver? = null

    /**
     * Attaches a component to this entity.
     *
     * ```kotlin
     * entity.add(Position(0f, 0f))
     * ```
     *
     * @return The entity itself to support fluent builders.
     */
    fun <T : Component> add(component: T): Entity {
        val type = component::class
        if (component is InputComponent) {
            component.ownerEntityId = id
        }
        components[type] = component
        observer?.onComponentAdded(this, type)
        return this
    }

    /**
     * Retrieves a component from this entity.
     *
     * ```kotlin
     * val position: Position? = entity.get(Position::class)
     * ```
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Component> get(componentClass: KClass<T>): T? {
        return components.entries.firstOrNull { componentClass.isInstance(it.value) }?.value as? T
    }

    /**
     * Removes a component from this entity.
     *
     * ```kotlin
     * entity.remove(Position::class)
     * ```
     */
    fun <T : Component> remove(componentClass: KClass<T>) {
        val removed = components.remove(componentClass)
        if (removed != null) {
            observer?.onComponentRemoved(this, componentClass)
        }
    }

    /**
     * Checks if the entity has a specific component.
     *
     * ```kotlin
     * if (entity.has(Health::class)) {
     *     // Safe to assume the entity is damageable.
     * }
     * ```
     */
    fun has(componentClass: KClass<out Component>): Boolean {
        return components.values.any { componentClass.isInstance(it) }
    }

    internal fun bindObserver(observer: ComponentObserver) {
        this.observer = observer
    }

    /**
     * Observer notified whenever components are added or removed.
     */
    internal interface ComponentObserver {
        fun onComponentAdded(entity: Entity, type: KClass<out Component>)
        fun onComponentRemoved(entity: Entity, type: KClass<out Component>)
    }
}
