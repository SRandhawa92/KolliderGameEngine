package com.kollider.engine.ecs

import kotlin.reflect.KClass

/**
 * Central registry coordinating entities, systems, and cached [EntityView] projections.
 *
 * Most gameplay code interacts with the world to spawn entities or register systems.
 *
 * ```kotlin
 * val world = World()
 * world.addSystem(RenderSystem(renderer))
 * val player = world.createEntity().apply {
 *     add(Position(100f, 200f))
 *     add(PlayerComponent())
 * }
 * ```
 */
class World : Entity.ComponentObserver {
    private val entities = mutableListOf<Entity>()
    private val systems = mutableListOf<System>()

    private val pendingEntityRemovals = mutableListOf<Entity>()
    private val pendingSystemRemovals = mutableListOf<System>()

    private val entityViews = mutableMapOf<Set<KClass<out Component>>, EntityView>()

    private var nextEntityId = 0

    /**
     * Creates a new entity, assigns a unique ID, and registers it in the world.
     *
     * ```kotlin
     * val enemy = world.createEntity()
     * ```
     */
    fun createEntity(): Entity {
        val entity = Entity(nextEntityId++)
        entity.bindObserver(this)
        entities.add(entity)
        trackEntityAcrossViews(entity)
        return entity
    }

    /**
     * Request removal of a specific entity.
     * Actual removal happens after the update loop ends.
     *
     * ```kotlin
     * world.removeEntity(enemy)
     * ```
     */
    fun removeEntity(entity: Entity) {
        pendingEntityRemovals.add(entity)
    }

    /**
     * Adds a system to the world.
     *
     * The system receives [System.onAttach] immediately and participates in the next [update] call.
     */
    fun addSystem(system: System) {
        systems.add(system)
        system.bindWorld(this)
    }

    /**
     * Request removal of a specific system.
     * Actual removal happens after the update loop ends.
     *
     * ```kotlin
     * world.removeSystem(spawnSystem)
     * ```
     */
    fun removeSystem(system: System) {
        pendingSystemRemovals.add(system)
    }

    /**
     * Returns a cached view of entities containing all [componentTypes].
     * The view updates automatically as components are added or removed.
     *
     * ```kotlin
     * val renderables = world.view(Position::class, Drawable::class)
     * ```
     */
    fun view(vararg componentTypes: KClass<out Component>): EntityView {
        require(componentTypes.isNotEmpty()) { "At least one component type is required" }
        val key = componentTypes.toSet()
        return entityViews.getOrPut(key) {
            EntityView(key).also { view ->
                entities.filter { entity -> key.all(entity::has) }
                    .forEach(view::add)
            }
        }
    }

    /**
     * Updates all systems, passing the list of entities.
     * We iterate over a snapshot of the systems so we can safely remove them after the loop.
     *
     * ```kotlin
     * world.update(deltaTime)
     * ```
     */
    fun update(deltaTime: Float) {
        val currentSystems = systems.toList()  // snapshot
        currentSystems.forEach { it.update(entities, deltaTime) }

        // Now apply any pending entity removals.
        if (pendingEntityRemovals.isNotEmpty()) {
            pendingEntityRemovals.forEach { entity ->
                entities.remove(entity)
                removeFromViews(entity)
            }
            pendingEntityRemovals.clear()
        }

        // Apply any pending system removals.
        if (pendingSystemRemovals.isNotEmpty()) {
            pendingSystemRemovals.forEach { system ->
                if (systems.remove(system)) {
                    system.dispose()
                    system.unbindWorld()
                }
            }
            pendingSystemRemovals.clear()
        }
    }

    /**
     * Disposes all systems and clears internal caches.
     *
     * Call this when the world is no longer needed (e.g., during shutdown or scene unload).
     *
     * ```kotlin
     * world.dispose()
     * ```
     */
    fun dispose() {
        systems.forEach {
            it.dispose()
            it.unbindWorld()
        }
        systems.clear()
        entityViews.clear()
    }

    /**
     * Broadcasts a resize event to every attached system.
     *
     * ```kotlin
     * world.resize(width, height)
     * ```
     */
    fun resize(width: Int, height: Int) {
        systems.forEach { it.resize(width, height) }
    }

    /**
     * Notifies systems that the simulation is paused.
     *
     * ```kotlin
     * world.pause()
     * ```
     */
    fun pause() {
        systems.forEach { it.pause() }
    }

    /**
     * Notifies systems that the simulation has resumed.
     *
     * ```kotlin
     * world.resume()
     * ```
     */
    fun resume() {
        systems.forEach { it.resume() }
    }

    /**
     * Keeps cached views in sync when components are added to [entity].
     */
    override fun onComponentAdded(entity: Entity, type: KClass<out Component>) {
        entityViews.forEach { (componentTypes, view) ->
            if (componentTypes.all(entity::has)) {
                view.add(entity)
            }
        }
    }

    /**
     * Removes [entity] from cached views when components are detached.
     */
    override fun onComponentRemoved(entity: Entity, type: KClass<out Component>) {
        entityViews.forEach { (componentTypes, view) ->
            if (!componentTypes.all(entity::has)) {
                view.remove(entity)
            }
        }
    }

    /**
     * Adds [entity] to any cached view whose component requirements are satisfied.
     */
    private fun trackEntityAcrossViews(entity: Entity) {
        entityViews.forEach { (componentTypes, view) ->
            if (componentTypes.all(entity::has)) {
                view.add(entity)
            }
        }
    }

    /**
     * Removes [entity] from every cached view.
     */
    private fun removeFromViews(entity: Entity) {
        entityViews.values.forEach { it.remove(entity) }
    }
}
