package com.kollider.flappybird.prefabs

import com.kollider.engine.core.GameConfig
import com.kollider.engine.ecs.World
import com.kollider.engine.ecs.physics.Collider
import com.kollider.engine.ecs.physics.Position
import com.kollider.engine.ecs.physics.Velocity
import com.kollider.engine.ecs.rendering.Drawable
import com.kollider.flappybird.components.ObstacleComponent

fun World.obstacle(speed: Float, config: GameConfig) {
    createEntity().apply {
        // Marker Obstacle Component
        add(ObstacleComponent())

        // Position Component - obstacle should be on the right side of the screen and in the middle
        // Y position is random between 0 and the screen height
        add(Position(config.width.toFloat(), (0..config.height).random().toFloat()))

        // Velocity Component - obstacle should move to the left
        add(Velocity(speed, 0f))

        add(Drawable.Rect(50f, 200f, 0xFFFFFFFF.toInt()))

        // Collider Component - obstacle should have a collider
        add(Collider(width = 50f, height = 200f))
    }
}