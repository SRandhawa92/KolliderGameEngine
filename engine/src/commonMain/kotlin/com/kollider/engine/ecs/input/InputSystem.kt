package com.kollider.engine.ecs.input

import com.kollider.engine.ecs.Entity
import com.kollider.engine.ecs.System

/**
 * The InputSystem updates the InputComponent of each entity based on platform input.
 */
class InputSystem(private val inputHandler: InputHandler) : System() {
    override fun update(entities: List<Entity>, deltaTime: Float) {
        entities.forEach { entity ->
            val inputComp = entity.get(InputComponent::class)
            if (inputComp != null) {
                inputComp.movement = inputHandler.getMovement()
                inputComp.shoot = inputHandler.isActionActive(Shoot)
                // Extend as needed
            }
        }
    }
}
