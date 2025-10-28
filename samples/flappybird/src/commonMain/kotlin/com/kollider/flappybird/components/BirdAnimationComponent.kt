package com.kollider.flappybird.components

import com.kollider.engine.ecs.Component
import com.kollider.engine.ecs.physics.Vector2

class BirdAnimationComponent(
    val wingShape: MutableList<Vector2>,
    val restPose: List<Vector2> = wingShape.map { it.copy() },
) : Component() {
    var phase: Float = 0f
}
