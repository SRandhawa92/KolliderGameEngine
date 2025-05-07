package com.kollider.pong.systems

import com.kollider.engine.core.GameConfig
import com.kollider.engine.ecs.Entity
import com.kollider.engine.ecs.System
import com.kollider.engine.ecs.World
import com.kollider.engine.ecs.input.InputComponent
import com.kollider.pong.components.StartButtonComponent
import com.kollider.pong.levels.pongLevel

fun World.startMenuSystem(config: GameConfig) {
    addSystem(StartMenuSystem(this, config))
}

class StartMenuSystem(
    private val world: World,
    private val config: GameConfig
) : System() {

    private var startClicked = false

    override fun update(entities: List<Entity>, deltaTime: Float) {
        // If user clicked the start button, load the Pong level.
        if (!startClicked) {
            // Find entity with StartButtonComponent, check if it's clicked.
            val startButton = entities.find { it.has(StartButtonComponent::class) }!!
            val startInput = startButton.get(InputComponent::class)!!

            if (startInput.shoot) {
                startClicked = true
                world.removeEntity(startButton)
                world.removeSystem(this)
                world.pongLevel(config)
            }
        }
    }
}