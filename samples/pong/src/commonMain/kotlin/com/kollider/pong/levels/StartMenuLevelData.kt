package com.kollider.pong.levels

import com.kollider.engine.core.GameConfig
import com.kollider.engine.core.SceneScope
import com.kollider.pong.prefabs.startButton
import com.kollider.pong.systems.startMenuSystem

fun SceneScope.startMenuLevel(
    config: GameConfig,
    onStart: () -> Unit
) {
    startButton(config)
    startMenuSystem(config, onStart)
}
