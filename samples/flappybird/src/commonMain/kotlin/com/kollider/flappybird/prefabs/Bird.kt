package com.kollider.flappybird.prefabs

import com.kollider.engine.core.GameConfig
import com.kollider.engine.ecs.World
import com.kollider.engine.ecs.input.InputComponent
import com.kollider.engine.ecs.physics.Collider
import com.kollider.engine.ecs.physics.Position
import com.kollider.engine.ecs.physics.Velocity
import com.kollider.engine.ecs.rendering.Drawable
import com.kollider.flappybird.components.BirdComponent

fun World.bird(config: GameConfig) {
    createEntity().apply {
        // Marker Bird Component
        add(BirdComponent())

        // Position Component - bird should be on the left side of the screen and in the middle
        add(Position(100f, config.height / 2f))

        // Velocity Component
        add(Velocity(0f, 0f))

        // Collider Component - bird should have a collider
        add(Collider(width = 50f, height = 50f))

        // Drawable Component - bird should have a drawable
         add(Drawable.Circle(10f, 0xFFFFFFFF.toInt()))

        // Input Component - bird should have an input component
         add(InputComponent())
    }
}