package com.kollider.engine.core

import com.kollider.engine.ecs.Entity
import com.kollider.engine.ecs.System
import com.kollider.engine.ecs.World

interface Scene {
    fun onEnter(scope: SceneScope) {}
    fun onExit(scope: SceneScope) {}
    fun onUpdate(deltaTime: Float, scope: SceneScope) {}
}

class SceneScope internal constructor(
    val context: GameContext,
    private val world: World,
) {
    private val systems = mutableListOf<System>()
    private val entities = mutableListOf<Entity>()

    val config get() = context.config
    val engine get() = context.engine
    val worldRef get() = world

    fun addSystem(system: System): System {
        world.addSystem(system)
        systems.add(system)
        return system
    }

    fun removeSystem(system: System) {
        world.removeSystem(system)
        systems.remove(system)
    }

    fun createEntity(builder: Entity.() -> Unit): Entity {
        val entity = world.createEntity().apply(builder)
        entities.add(entity)
        return entity
    }

    fun track(entity: Entity) {
        entities.add(entity)
    }

    internal fun dispose() {
        systems.toList().forEach(world::removeSystem)
        systems.clear()
        entities.toList().forEach(world::removeEntity)
        entities.clear()
    }
}
