package com.kollider.pong.systems

import com.kollider.engine.core.GameConfig
import com.kollider.engine.ecs.Entity
import com.kollider.engine.ecs.EntityView
import com.kollider.engine.ecs.System
import com.kollider.engine.ecs.World
import com.kollider.engine.ecs.input.InputComponent
import com.kollider.engine.ecs.input.Shoot
import com.kollider.engine.ecs.require
import com.kollider.pong.components.StartButtonComponent
import com.kollider.pong.levels.pongLevel

fun World.startMenuSystem(config: GameConfig) {
    addSystem(StartMenuSystem(config))
}

class StartMenuSystem(
    private val config: GameConfig
) : System() {
    private lateinit var startButtons: EntityView
    private var startClicked = false

    override fun onAttach(world: World) {
        startButtons = world.view(StartButtonComponent::class, InputComponent::class)
    }

    @Suppress("UNUSED_PARAMETER")
    override fun update(entities: List<Entity>, deltaTime: Float) {
        if (startClicked) return

        val iterator = startButtons.iterator()
        if (!iterator.hasNext()) return
        val startButton = iterator.next()
        val startInput = startButton.require<InputComponent>()

        if (startInput.shoot) {
            startClicked = true
            config.inputRouter.clearAction(Shoot)
            val gameWorld = world
            gameWorld.removeEntity(startButton)
            gameWorld.removeSystem(this)
            gameWorld.pongLevel(config)
        }
    }
}
