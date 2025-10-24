package com.kollider.engine.ecs.input

import com.kollider.engine.ecs.Entity
import com.kollider.engine.ecs.System
import com.kollider.engine.ecs.World
import com.kollider.engine.ecs.EntityView

/**
 * The InputSystem updates each [InputComponent] with analog state and forwards
 * dispatcher events to per-entity listeners.
 */
class InputSystem(private val inputHandler: InputHandler) : System() {
    private val dispatcher = inputHandler.dispatcher
    private val trackedComponents = mutableMapOf<Int, InputComponent>()
    private lateinit var inputView: EntityView

    override fun onAttach(world: World) {
        inputView = world.view(InputComponent::class)
    }

    override fun update(entities: List<Entity>, deltaTime: Float) {
        val movement = inputHandler.getMovement()
        val shootActive = inputHandler.isActionActive(Shoot)
        val activeEntityIds = HashSet<Int>(trackedComponents.size.coerceAtLeast(inputView.size))

        inputView.forEach { entity ->
            val inputComp = entity.get(InputComponent::class) ?: return@forEach
            trackedComponents[entity.id] = inputComp
            activeEntityIds.add(entity.id)

            inputComp.movement = movement
            inputComp.shoot = shootActive
        }

        val iterator = trackedComponents.keys.iterator()
        while (iterator.hasNext()) {
            val entityId = iterator.next()
            if (!activeEntityIds.contains(entityId)) {
                iterator.remove()
            }
        }

        val events = dispatcher.poll()
        if (events.isEmpty()) return

        events.forEach { event ->
            if (event.targetEntityId != null) {
                trackedComponents[event.targetEntityId]?.let { component ->
                    applyEvent(component, event)
                }
            } else {
                trackedComponents.values.forEach { component ->
                    applyEvent(component, event)
                }
            }
        }
    }

    private fun applyEvent(component: InputComponent, event: InputEvent) {
        when (event.action) {
            Shoot -> component.shoot = event.isActive
            Pause -> component.paused = event.isActive
        }
        component.dispatch(event)
    }
}
