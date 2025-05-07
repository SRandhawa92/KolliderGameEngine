package com.kollider.engine.ecs.input

import com.kollider.engine.core.GameConfig
import com.kollider.engine.ecs.physics.Vector2
import java.awt.event.KeyEvent
import java.awt.event.KeyListener

actual fun createInputHandler(config: GameConfig): InputHandler {
    return JvmInputHandler()
}

class JvmInputHandler: InputHandler, KeyListener {
    // Holds the currently pressed key codes.
    private val activeActions = mutableSetOf<Action>()
    private val pressedKeys = mutableSetOf<Int>()

    override fun isActionActive(action: Action): Boolean {
        return activeActions.contains(action)
    }

    override fun getMovement(): Vector2 {
        var x = 0f
        var y = 0f
        if (pressedKeys.contains(KeyEvent.VK_W)) {
            y -= 1f
        }
        if (pressedKeys.contains(KeyEvent.VK_S)) {
            y += 1f
        }
        if (pressedKeys.contains(KeyEvent.VK_A)) {
            x -= 1f
        }
        if (pressedKeys.contains(KeyEvent.VK_D)) {
            x += 1f
        }
        return Vector2(x, y)
    }

    override fun keyTyped(event: KeyEvent?) {
        // Not used
    }

    override fun keyPressed(event: KeyEvent?) {
        event?.let {
            pressedKeys.add(it.keyCode)
            activeActions.add(when (it.keyCode) {
                KeyEvent.VK_SPACE -> Shoot
                else -> return
            })
        }
    }

    override fun keyReleased(event: KeyEvent?) {
        event?.let {
            pressedKeys.remove(it.keyCode)
            activeActions.remove(when (it.keyCode) {
                KeyEvent.VK_SPACE -> Shoot
                else -> return
            })
        }
    }
}