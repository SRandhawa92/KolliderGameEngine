package com.kollider.engine.ecs.input

import com.kollider.engine.core.GameConfig
import com.kollider.engine.ecs.physics.Vector2

expect fun createInputHandler(config: GameConfig): InputHandler

interface InputHandler {
    fun isActionActive(action: Action): Boolean
    fun getMovement(): Vector2
}
