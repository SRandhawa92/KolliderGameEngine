package com.kollider.flappybird.prefabs

import com.kollider.engine.core.GameConfig
import com.kollider.engine.core.SceneScope
import com.kollider.engine.ecs.Entity
import com.kollider.engine.ecs.physics.Collider
import com.kollider.engine.ecs.physics.Position
import com.kollider.engine.ecs.physics.Velocity
import com.kollider.engine.ecs.rendering.Drawable
import com.kollider.flappybird.components.ObstacleComponent
import kotlin.math.max
import kotlin.random.Random

private const val OBSTACLE_WIDTH = 50f
private const val OBSTACLE_HEIGHT = 200f

fun SceneScope.obstacle(speed: Float, config: GameConfig, gapPadding: Float): Entity {
    val safePadding = max(gapPadding, OBSTACLE_HEIGHT / 2f)
    val minCenter = safePadding
    val maxCenter = max(minCenter, config.height.toFloat() - safePadding)
    val centerY = if (maxCenter == minCenter) minCenter else Random.nextFloat() * (maxCenter - minCenter) + minCenter
    val topLeftY = (centerY - OBSTACLE_HEIGHT / 2f).coerceIn(0f, config.height.toFloat() - OBSTACLE_HEIGHT)
    val color = 0x00228B22 // Green color for obstacle

    return createEntity {
        // Marker component
        add(ObstacleComponent())
        // Position component - spawn at right edge with visible clamping.
        add(Position(config.width.toFloat(), topLeftY))
        // Velocity component - moves left over time.
        add(Velocity(speed, 0f))
        add(Drawable.Rect(OBSTACLE_WIDTH, OBSTACLE_HEIGHT, color))
        // Collider component for collision detection.
        add(Collider(width = OBSTACLE_WIDTH, height = OBSTACLE_HEIGHT))
    }
}
