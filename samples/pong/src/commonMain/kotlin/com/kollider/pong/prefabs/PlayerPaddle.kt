package com.kollider.pong.prefabs

import com.kollider.engine.core.GameConfig
import com.kollider.engine.ecs.World
import com.kollider.engine.ecs.input.InputComponent
import com.kollider.engine.ecs.physics.Collider
import com.kollider.engine.ecs.physics.Position
import com.kollider.engine.ecs.physics.Velocity
import com.kollider.engine.ecs.rendering.Drawable
import com.kollider.pong.components.PlayerPaddleComponent

fun World.playerPaddle(config: GameConfig) {
    createEntity().apply {
        // Place the paddle on the bottom of the screen.
        add(Position(config.width / 2f, config.height - 60f))

        // Set initial velocity.
        add(Velocity(0f, 0f))

        // Define collider size.
        add(Collider(100f, 10f))

        // Attach the renderable (with a rectangle drawable).
        add(Drawable.Rect(100f, 10f, 0xFFFFFFFF.toInt()))

        // Used by the InputSystem so the player can control the paddle.
        add(InputComponent())

        // Marker component for player-controlled paddles.
        add(PlayerPaddleComponent())
    }
}