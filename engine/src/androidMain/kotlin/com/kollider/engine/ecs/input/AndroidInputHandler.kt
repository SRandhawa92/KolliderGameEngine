package com.kollider.engine.ecs.input

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import com.kollider.engine.core.GameConfig
import com.kollider.engine.ecs.physics.Vector2

actual fun createInputHandler(config: GameConfig): InputHandler {
    return AndroidInputHandler(config.appContext.get()!!)
}

class AndroidInputHandler(context: Context) : InputHandler {

    private val gestureControl = GestureControl()
    private val gestureDetector = GestureDetector(context, gestureControl)

    override fun isActionActive(action: Action): Boolean {
        return gestureControl.activeActions.contains(action)
    }

    override fun getMovement(): Vector2 {
        return gestureControl.movement
    }

    fun onTouchEvent(event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

    fun clearActions() {
        gestureControl.reset()

    }
}