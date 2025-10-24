package com.kollider.pong.prefabs

import com.kollider.engine.core.GameConfig
import com.kollider.engine.core.SceneScope
import com.kollider.engine.ecs.physics.Collider
import com.kollider.engine.ecs.physics.Position
import com.kollider.engine.ecs.physics.Velocity
import com.kollider.engine.ecs.rendering.Drawable
import com.kollider.pong.components.ComputerPaddleComponent

fun SceneScope.computerPaddle(config: GameConfig) {
    createEntity {
        // Place the paddle on the top of the screen.
        add(Position(config.width / 2f, 60f))

        // Set initial velocity.
        add(Velocity(0f, 0f))

        // Define collider size.
        add(Collider(100f, 10f))

        // Attach the renderable (with a rectangle drawable).
        add(Drawable.Rect(100f, 10f, 0xFFFFFFFF.toInt()))

        // Marker component for Computer-controlled paddles.
        add(ComputerPaddleComponent())
    }
}
