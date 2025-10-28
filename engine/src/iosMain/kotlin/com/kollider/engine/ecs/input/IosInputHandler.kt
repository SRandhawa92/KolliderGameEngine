package com.kollider.engine.ecs.input

import com.kollider.engine.core.GameConfig
import com.kollider.engine.ecs.physics.Vector2
import com.kollider.engine.ios.IosInputBridge

actual fun createInputHandler(config: GameConfig): InputHandler {
    return IosInputHandler()
}

class IosInputHandler : InputHandler {
    override val dispatcher: InputDispatcher = InputDispatcher()
    private val actionStates = mutableMapOf<String, Boolean>()
    private val movementVector = Vector2()

    init {
        IosInputBridge.attach(this)
    }

    override fun isActionActive(action: Action): Boolean {
        return actionStates[action.name] == true
    }

    override fun getMovement(): Vector2 {
        return movementVector
    }

    fun setMovement(x: Float, y: Float) {
        movementVector.x = x
        movementVector.y = y
    }

    fun setActionState(actionName: String, isActive: Boolean, targetEntityId: Int? = null) {
        actionStates[actionName] = isActive
        ActionRegistry.resolve(actionName)?.let { action ->
            dispatcher.emit(action, isActive, targetEntityId)
        }
    }

    fun clearAction(actionName: String) {
        actionStates.remove(actionName)
    }

    fun dispose() {
        IosInputBridge.detach(this)
        actionStates.clear()
        movementVector.x = 0f
        movementVector.y = 0f
    }
}
