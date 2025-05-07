package com.kollider.engine.ecs.input

import com.kollider.engine.core.GameConfig


actual fun createInputHandler(config: GameConfig): InputHandler {
    return JsInputHandler()
}


class JsInputHandler: InputHandler {
    override fun pollInputs() {
        // Poll the JS input system
    }

    override fun isActionActive(action: Action): Boolean {
        // Check if the action is active in the JS input system
        return false
    }
}