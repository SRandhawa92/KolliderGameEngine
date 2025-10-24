package com.kollider.engine.ecs.input

import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.internal.SynchronizedObject
import kotlinx.coroutines.internal.synchronized

/**
 * Represents a high-level input event, optionally targeting a specific entity.
 */
data class InputEvent(
    val action: Action,
    val isActive: Boolean,
    val targetEntityId: Int? = null,
)

/**
 * Functional listener triggered whenever an [InputEvent] is dispatched.
 *
 * ```kotlin
 * val listener = InputListener { event -> println(event.action.name) }
 * ```
 */
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


    /**
     * Queues a raw [event] to be consumed later.
     *
     * ```kotlin
     * dispatcher.emit(InputEvent(Shoot, isActive = true))
     * ```
     */
    fun emit(event: InputEvent) {
        synchronized(lock) {
            queuedEvents.add(event)
        }
    }

    /**
     * Convenience overload for emitting a simple action event.
     *
     * ```kotlin
     * dispatcher.emit(Shoot, isActive = false, targetEntityId = entity.id)
     * ```
     */
    fun emit(action: Action, isActive: Boolean, targetEntityId: Int? = null) {
        emit(InputEvent(action, isActive, targetEntityId))
    }

    /**
     * Returns the current queue of events and clears the internal buffer.
     *
     * ```kotlin
     * dispatcher.poll().forEach(inputSystem::handle)
     * ```
     */
    fun poll(): List<InputEvent> {
        synchronized(lock) {
            if (queuedEvents.isEmpty()) return emptyList()
            val snapshot = queuedEvents.toList()
            queuedEvents.clear()
            return snapshot
        }
    }
}
