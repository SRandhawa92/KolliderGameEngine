package com.kollider.engine.ecs

import kotlin.collections.LinkedHashSet
import kotlin.reflect.KClass

/**
 * Represents a live projection of entities containing a specific set of components.
 *
 * Views are cached by [World.view] and stay in sync as entities gain or lose components.
 *
 * ```kotlin
 * private lateinit var players: EntityView
 *
 * override fun onAttach(world: World) {
 *     players = world.view(PlayerTag::class, Position::class)
 * }
 *
 * override fun update(entities: List<Entity>, deltaTime: Float) {
 *     for (player in players) {
 *         // Operate only on player entities.
 *     }
 * }
 * ```
 */
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

    /**
     * @return `true` if the view currently tracks [entity].
     *
     * ```kotlin
     * if (players.contains(entity)) { /* ... */ }
     * ```
     */
    fun contains(entity: Entity): Boolean = entities.contains(entity)

    override fun iterator(): Iterator<Entity> = entities.iterator()

    /**
     * Materialises the view into a snapshot list.
     *
     * ```kotlin
     * val currentlyVisible = players.toList()
     * ```
     */
    fun toList(): List<Entity> = entities.toList()

    /**
     * @return Number of entities currently visible to the view.
     *
     * ```kotlin
     * val playerCount = players.size
     * ```
     */
    val size: Int get() = entities.size
}
