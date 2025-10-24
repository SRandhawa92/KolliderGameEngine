package com.kollider.pong.prefabs

import com.kollider.engine.core.GameConfig
import com.kollider.engine.ecs.World
import com.kollider.engine.ecs.input.InputComponent
import com.kollider.engine.ecs.input.Shoot
import com.kollider.engine.ecs.physics.Collider
import com.kollider.engine.ecs.physics.Position
import com.kollider.engine.ecs.rendering.Drawable
import com.kollider.pong.components.StartButtonComponent

fun World.startButton(config: GameConfig) {
    val button = createEntity()

    button.add(Position(config.width / 2f, config.height / 2f))
    button.add(Collider(width = 100f, height = 50f))
    button.add(Drawable.Text("START", 20f, 0xFFFFFFFF.toInt()))
    button.add(StartButtonComponent())
    button.add(InputComponent(movementEnabled = false).apply {
        bindAction(Shoot)
    })

    config.inputRouter.routeAction(Shoot, button.id)
    config.inputRouter.routeMovement(null)
}
