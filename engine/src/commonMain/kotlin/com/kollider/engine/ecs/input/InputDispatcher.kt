package com.kollider.engine.ecs.input

import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.SynchronizedObject
import kotlinx.coroutines.internal.synchronized

/**
 * Represents a high-level input action, optionally directed at a specific entity.
 */
data class InputEvent(
    val action: Action,
    val isActive: Boolean,
    val targetEntityId: Int? = null,
)

fun interface InputListener {
    fun onInput(event: InputEvent)
}

/**
 * Collects action events emitted by platform input handlers. Systems can poll the dispatcher
 * each frame and forward the events to interested entities.
 */

@OptIn(InternalCoroutinesApi::class)
class InputDispatcher {
    private val queuedEvents = mutableListOf<InputEvent>()
    private val lock = SynchronizedObject()


    fun emit(event: InputEvent) {
        synchronized(lock) {
            queuedEvents.add(event)
        }
    }

    fun emit(action: Action, isActive: Boolean, targetEntityId: Int? = null) {
        emit(InputEvent(action, isActive, targetEntityId))
    }

    fun poll(): List<InputEvent> {
        synchronized(lock) {
            if (queuedEvents.isEmpty()) return emptyList()
            val snapshot = queuedEvents.toList()
            queuedEvents.clear()
            return snapshot
        }
    }
}
