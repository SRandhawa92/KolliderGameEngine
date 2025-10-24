package com.kollider.pong.components

import com.kollider.engine.ecs.Component

/**
 * Tracks the running score for the Pong match.
 */
data class ScoreComponent(
    var player: Int = 0,
    var computer: Int = 0
) : Component()
