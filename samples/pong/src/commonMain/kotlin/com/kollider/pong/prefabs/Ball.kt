package com.kollider.pong.prefabs

import com.kollider.engine.core.GameConfig
import com.kollider.engine.ecs.World
import com.kollider.engine.ecs.physics.Collider
import com.kollider.engine.ecs.physics.Position
import com.kollider.engine.ecs.physics.Velocity
import com.kollider.engine.ecs.rendering.Drawable
import com.kollider.pong.components.BallComponent

fun World.ball(config: GameConfig) {
    createEntity().apply {
        // Place the ball in the center of the screen.
        add(Position(config.width / 2f, config.height / 2f))

        // Set initial velocity.
        add(Velocity(200f, 150f))

        // Define collider size.
        add(Collider(width = 10f, height = 10f))

        // Attach the renderable (with a sprite drawable).
        add(Drawable.Circle(10f, 0xFFFFFFFF.toInt()))

        // Marker component for ball entities.
        add(BallComponent())
    }
}