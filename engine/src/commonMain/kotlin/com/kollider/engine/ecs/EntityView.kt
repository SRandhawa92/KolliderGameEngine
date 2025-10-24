package com.kollider.engine.ecs

import kotlin.collections.LinkedHashSet
import kotlin.reflect.KClass

class EntityView internal constructor(
    internal val componentTypes: Set<KClass<out Component>>,
) : Iterable<Entity> {
    private val entities = LinkedHashSet<Entity>()

    internal fun add(entity: Entity) {
        entities.add(entity)
    }

    internal fun remove(entity: Entity) {
        entities.remove(entity)
    }

    fun contains(entity: Entity): Boolean = entities.contains(entity)

    override fun iterator(): Iterator<Entity> = entities.iterator()

    fun toList(): List<Entity> = entities.toList()

    val size: Int get() = entities.size
}
