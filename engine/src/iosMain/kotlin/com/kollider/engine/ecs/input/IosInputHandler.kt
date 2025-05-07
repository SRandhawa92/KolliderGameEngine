package com.kollider.engine.ecs.input

import com.kollider.engine.core.GameConfig
import com.kollider.engine.ecs.physics.Vector2

actual fun createInputHandler(config: GameConfig): InputHandler {
    return IosInputHandler()
}

class IosInputHandler: InputHandler {
    override fun isActionActive(action: Action): Boolean {
        return false
    }

    override fun getMovement(): Vector2 {
        return Vector2(0f, 0f)
    }
}