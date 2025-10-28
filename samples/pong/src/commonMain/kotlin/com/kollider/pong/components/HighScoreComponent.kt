package com.kollider.pong.components

import com.kollider.engine.ecs.Component

const val HIGH_SCORE_STORAGE_KEY = "pong.high_score"

data class HighScoreComponent(
    var best: Int = 0,
    val storageKey: String = HIGH_SCORE_STORAGE_KEY,
) : Component()
