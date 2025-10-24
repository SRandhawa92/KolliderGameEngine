package com.kollider.engine.core

import com.kollider.engine.ecs.Entity
import com.kollider.engine.ecs.System
import com.kollider.engine.ecs.World

/**
 * High-level gameplay state managed by the [GameEngine]'s scene stack.
 *
 * Override the lifecycle callbacks to create, update, and tear down logic.
 */
interface Scene {
    /**
     * Called when the scene becomes active. Use [scope] to spawn entities or systems.
     *
     * ```kotlin
     * override fun onEnter(scope: SceneScope) {
     *     scope.createEntity { /* ... */ }
     * }
     * ```
     */
    fun onEnter(scope: SceneScope) {}

    /**
     * Called when the scene is about to be removed or replaced.
     *
     * ```kotlin
     * override fun onExit(scope: SceneScope) {
     *     saveProgress()
     * }
     * ```
     */
    fun onExit(scope: SceneScope) {}

    /**
     * Called every frame while the scene is active.
     *
     * ```kotlin
     * override fun onUpdate(deltaTime: Float, scope: SceneScope) {
     *     updateEnemies(deltaTime)
     * }
     * ```
     */
    fun onUpdate(deltaTime: Float, scope: SceneScope) {}
}

/**
 * Utility wrapper exposed to scenes that simplifies world interactions and clean-up.
 */
class SceneScope internal constructor(
    val context: GameContext,
    private val world: World,
) {
    private val systems = mutableListOf<System>()
    private val entities = mutableListOf<Entity>()

    /**
     * Shortcut to [GameContext.config].
     */
    val config get() = context.config

    /**
     * Shortcut to the running [GameEngine].
     */
    val engine get() = context.engine

    /**
     * Provides direct access to the underlying [World] if needed.
     */
    val worldRef get() = world

    /**
     * Adds a [system] to the world and tracks it for automatic removal when the scene exits.
     *
     * ```kotlin
     * scope.addSystem(SpawnSystem())
     * ```
     */
    fun addSystem(system: System): System {
        world.addSystem(system)
        systems.add(system)
        return system
    }

    /**
     * Removes a previously added system immediately.
     */
    fun removeSystem(system: System) {
        world.removeSystem(system)
        systems.remove(system)
    }

    /**
     * Creates a new entity via [World.createEntity] and registers it for clean-up.
     *
     * ```kotlin
     * val enemy = scope.createEntity {
     *     add(Position(0f, 0f))
     * }
     * ```
     */
    fun createEntity(builder: Entity.() -> Unit): Entity {
        val entity = world.createEntity().apply(builder)
        entities.add(entity)
        return entity
    }

    /**
     * Tracks an externally created [entity] so it is removed when the scene exits.
     *
     * ```kotlin
     * scope.track(playerEntity)
     * ```
     */
    fun track(entity: Entity) {
        entities.add(entity)
    }

    /**
     * Disposes tracked systems and entities. Invoked automatically by the engine.
     *
     * ```kotlin
     * scope.dispose() // rarely called manually
     * ```
     */
    internal fun dispose() {
        systems.toList().forEach(world::removeSystem)
        systems.clear()
        entities.toList().forEach(world::removeEntity)
        entities.clear()
    }
}
