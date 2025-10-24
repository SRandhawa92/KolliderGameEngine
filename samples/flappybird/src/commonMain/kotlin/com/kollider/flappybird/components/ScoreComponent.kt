package com.kollider.flappybird.components

import com.kollider.engine.ecs.Component

/**
 * Tracks the player's score for the current run.
 */
data class ScoreComponent(
    var value: Int = 0
) : Component()
