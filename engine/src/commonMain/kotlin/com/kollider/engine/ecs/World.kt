package com.kollider.engine.ecs

import kotlin.reflect.KClass

class World : Entity.ComponentObserver {
    private val entities = mutableListOf<Entity>()
    private val systems = mutableListOf<System>()

    private val pendingEntityRemovals = mutableListOf<Entity>()
    private val pendingSystemRemovals = mutableListOf<System>()

    private val entityViews = mutableMapOf<Set<KClass<out Component>>, EntityView>()

    private var nextEntityId = 0

    /**
     * Creates a new entity with a unique ID and adds it to the world.
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
     */
    fun removeEntity(entity: Entity) {
        pendingEntityRemovals.add(entity)
    }

    /**
     * Adds a system to the world.
     */
    fun addSystem(system: System) {
        systems.add(system)
        system.bindWorld(this)
    }

    /**
     * Request removal of a specific system.
     * Actual removal happens after the update loop ends.
     */
    fun removeSystem(system: System) {
        pendingSystemRemovals.add(system)
    }

    /**
     * Returns a cached view of entities containing all [componentTypes].
     * The view updates automatically as components are added or removed.
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

    fun dispose() {
        systems.forEach {
            it.dispose()
            it.unbindWorld()
        }
        systems.clear()
        entityViews.clear()
    }

    fun resize(width: Int, height: Int) {
        systems.forEach { it.resize(width, height) }
    }

    fun pause() {
        systems.forEach { it.pause() }
    }

    fun resume() {
        systems.forEach { it.resume() }
    }

    override fun onComponentAdded(entity: Entity, type: KClass<out Component>) {
        entityViews.forEach { (componentTypes, view) ->
            if (componentTypes.all(entity::has)) {
                view.add(entity)
            }
        }
    }

    override fun onComponentRemoved(entity: Entity, type: KClass<out Component>) {
        entityViews.forEach { (componentTypes, view) ->
            if (!componentTypes.all(entity::has)) {
                view.remove(entity)
            }
        }
    }

    private fun trackEntityAcrossViews(entity: Entity) {
        entityViews.forEach { (componentTypes, view) ->
            if (componentTypes.all(entity::has)) {
                view.add(entity)
            }
        }
    }

    private fun removeFromViews(entity: Entity) {
        entityViews.values.forEach { it.remove(entity) }
    }
}
