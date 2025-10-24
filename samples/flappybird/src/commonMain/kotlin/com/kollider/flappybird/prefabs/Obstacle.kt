package com.kollider.flappybird.prefabs

import com.kollider.engine.core.GameConfig
import com.kollider.engine.ecs.World
import com.kollider.engine.ecs.physics.Collider
import com.kollider.engine.ecs.physics.Position
import com.kollider.engine.ecs.physics.Velocity
import com.kollider.engine.ecs.rendering.Drawable
import com.kollider.flappybird.components.ObstacleComponent
import kotlin.math.max
import kotlin.random.Random

private const val OBSTACLE_WIDTH = 50f
private const val OBSTACLE_HEIGHT = 200f

fun World.obstacle(speed: Float, config: GameConfig, gapPadding: Float) {
    val safePadding = max(gapPadding, OBSTACLE_HEIGHT / 2f)
    val minCenter = safePadding
    val maxCenter = max(minCenter, config.height.toFloat() - safePadding)
    val centerY = if (maxCenter == minCenter) minCenter else Random.nextFloat() * (maxCenter - minCenter) + minCenter
    val topLeftY = (centerY - OBSTACLE_HEIGHT / 2f).coerceIn(0f, config.height.toFloat() - OBSTACLE_HEIGHT)

    createEntity().apply {
        // Marker Obstacle Component
        add(ObstacleComponent())

        // Position Component - obstacle should be on the right side of the screen and in the middle
        // Y position is clamped so the full obstacle remains visible.
        add(Position(config.width.toFloat(), topLeftY))

        // Velocity Component - obstacle should move to the left
        add(Velocity(speed, 0f))

        add(Drawable.Rect(OBSTACLE_WIDTH, OBSTACLE_HEIGHT, 0xFFFFFFFF.toInt()))

        // Collider Component - obstacle should have a collider
        add(Collider(width = OBSTACLE_WIDTH, height = OBSTACLE_HEIGHT))
    }
}
