package com.kollider.engine.ecs.input

import com.kollider.engine.core.GameConfig
import com.kollider.engine.ecs.physics.Vector2

actual fun createInputHandler(config: GameConfig): InputHandler {
    return JsInputHandler()
}

class JsInputHandler : InputHandler {
    override val dispatcher: InputDispatcher = InputDispatcher()

    override fun isActionActive(action: Action): Boolean = false

    override fun getMovement(): Vector2 = Vector2()
}
