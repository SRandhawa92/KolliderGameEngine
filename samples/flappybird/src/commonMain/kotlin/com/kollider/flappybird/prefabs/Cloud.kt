package com.kollider.flappybird.prefabs

import com.kollider.engine.core.GameConfig
import com.kollider.engine.core.SceneScope
import com.kollider.engine.ecs.Entity
import com.kollider.engine.ecs.physics.Position
import com.kollider.engine.ecs.physics.Velocity
import com.kollider.engine.ecs.rendering.Drawable
import com.kollider.flappybird.components.CloudComponent
import kotlin.math.max
import kotlin.random.Random

const val CLOUD_RADIUS = 100f

fun SceneScope.cloud(speed: Float, config: GameConfig, gapPadding: Float): Entity {
    val safePadding = max(gapPadding, CLOUD_RADIUS / 2f)
    val minCenter = safePadding
    val maxCenter = max(minCenter, config.height.toFloat() - safePadding)
    val centerY = if (maxCenter == minCenter) minCenter else Random.nextFloat() * (maxCenter - minCenter) + minCenter
    val topLeftY = (centerY - CLOUD_RADIUS / 2f).coerceIn(0f, config.height.toFloat() - CLOUD_RADIUS)
    val color = 0xFFFFFFFF.toInt() // White color for cloud

    return createEntity {
        add(CloudComponent())
        // Position component - spawn at right edge with visible clamping.
        add(Position(config.width.toFloat(), topLeftY))
        // Velocity component - moves left over time.
        add(Velocity(speed, 0f))

        // draw cloud with multiple circles to simulate a cloud
        add(
            Drawable.Composite(
                elements = listOf(
                    Drawable.Circle(30f, color, offsetX = 20f, offsetY = 20f),
                    Drawable.Circle(25f, color, offsetX = 50f, offsetY = 15f),
                    Drawable.Circle(20f, color, offsetX = 80f, offsetY = 25f),
                    Drawable.Circle(28f, color, offsetX = 40f, offsetY = 35f),
                    Drawable.Circle(22f, color, offsetX = 70f, offsetY = 40f),
                )
            )
        )
    }
}
