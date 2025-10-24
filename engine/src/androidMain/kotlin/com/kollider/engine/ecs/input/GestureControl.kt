package com.kollider.engine.ecs.input

import android.view.GestureDetector
import android.view.MotionEvent
import com.kollider.engine.ecs.physics.Vector2
import kotlin.math.abs

/**
 * GestureControl is a class that extends GestureDetector.SimpleOnGestureListener
 * It is used to detect gestures on the Android platform
 *
 */
class GestureControl(private val dispatcher: InputDispatcher) : GestureDetector.SimpleOnGestureListener() {

    val activeActions = mutableSetOf<Action>()
    var movement = Vector2(0f, 0f)
        private set

    /**
     * Adds the Shoot action to the active actions
     *
     * @param e The motion event
     * @return true if the event is consumed, else false
     */
    override fun onDown(e: MotionEvent): Boolean {
        if (activeActions.add(Shoot)) {
            dispatcher.emit(Shoot, true)
        }
        return true
    }

    /**
     * Detects the direction of the swipe and sets the movement vector accordingly
     *
     * Also detects any changes in direction and sets the movement vector accordingly
     */
    override fun onScroll(e1: MotionEvent?, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
        if (e1 == null) return false
        val dx = e2.x - e1.x
        val dy = e2.y - e1.y
        if (abs(dx) > abs(dy)) {
            movement.x = dx
            movement.y = 0f
        } else {
            movement.x = 0f
            movement.y = dy
        }
        return true
    }

    /**
     * Resets the movement vector and clears the active actions
     */
    fun reset() {
        movement = Vector2(0f, 0f)
        if (activeActions.remove(Shoot)) {
            dispatcher.emit(Shoot, false)
        }
        activeActions.clear()
    }
}
