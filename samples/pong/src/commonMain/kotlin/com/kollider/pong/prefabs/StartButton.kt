package com.kollider.pong.prefabs

import com.kollider.engine.core.GameConfig
import com.kollider.engine.ecs.World
import com.kollider.engine.ecs.input.InputComponent
import com.kollider.engine.ecs.physics.Collider
import com.kollider.engine.ecs.physics.Position
import com.kollider.engine.ecs.rendering.Drawable
import com.kollider.pong.components.StartButtonComponent

fun World.startButton(config: GameConfig) {
    createEntity().apply {
        // Place the button in the center of the screen.
        add(Position(config.width / 2f, config.height / 2f))

        // Define collider size.
        add(Collider(width = 100f, height = 50f))

        // Attach the renderable (with a rectangle drawable).
        add(Drawable.Text("START", 20f, 0xFFFFFFFF.toInt()))

        // Add start button component to handle button clicks.
        add(StartButtonComponent())

        // Add input component to handle button clicks.
        add(InputComponent())
    }
}