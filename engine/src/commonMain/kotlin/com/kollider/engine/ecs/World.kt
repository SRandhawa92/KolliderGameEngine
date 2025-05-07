package com.kollider.engine.ecs

class World {
    private val entities = mutableListOf<Entity>()
    private val systems = mutableListOf<System>()

    // For deferring removals until after the update loop finishes.
    private val pendingEntityRemovals = mutableListOf<Entity>()
    private val pendingSystemRemovals = mutableListOf<System>()

    private var nextEntityId = 0

    /**
     * Creates a new entity with a unique ID and adds it to the world.
     */
    fun createEntity(): Entity {
        val entity = Entity(nextEntityId++)
        entities.add(entity)
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
    }

    /**
     * Request removal of a specific system.
     * Actual removal happens after the update loop ends.
     */
    fun removeSystem(system: System) {
        pendingSystemRemovals.add(system)
    }

    /**
     * Updates all systems, passing the list of entities.
     * We iterate over a snapshot of the systems so we can safely remove them after the loop.
     */
    fun update(deltaTime: Float) {
        val currentSystems = systems.toList()  // snapshot
        currentSystems.forEach { it.update(entities, deltaTime) }

        // Now apply any pending entity removals.
        pendingEntityRemovals.forEach { entities.remove(it) }
        pendingEntityRemovals.clear()

        // Apply any pending system removals.
        pendingSystemRemovals.forEach { systems.remove(it) }
        pendingSystemRemovals.clear()
    }

    fun dispose() {
        systems.forEach { it.dispose() }
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
}