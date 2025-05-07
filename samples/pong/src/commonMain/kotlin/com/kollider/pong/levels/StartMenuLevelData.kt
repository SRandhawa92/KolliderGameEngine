package com.kollider.pong.levels

import com.kollider.engine.core.GameConfig
import com.kollider.engine.ecs.World
import com.kollider.pong.prefabs.startButton
import com.kollider.pong.systems.startMenuSystem

fun World.startMenuLevel(config: GameConfig) {
    startButton(config)
    startMenuSystem(config)
}