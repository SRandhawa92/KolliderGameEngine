package com.kollider.engine.ecs.rendering

import com.kollider.engine.core.GameConfig

expect fun createCanvas(config: GameConfig): Canvas

interface Canvas {
    val canvasWidth: Int
    val canvasHeight: Int
    fun paint()
}