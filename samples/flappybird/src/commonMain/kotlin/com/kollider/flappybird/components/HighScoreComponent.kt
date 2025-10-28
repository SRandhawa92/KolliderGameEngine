package com.kollider.flappybird.components

import com.kollider.engine.ecs.Component

const val FLAPPY_HIGH_SCORE_KEY = "flappybird.high_score"

data class HighScoreComponent(
    var best: Int = 0,
    val storageKey: String = FLAPPY_HIGH_SCORE_KEY,
) : Component()
