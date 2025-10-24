package com.kollider.engine.ecs.input

/**
 * Allows gameplay code to direct input events to specific entities.
 * Systems can route actions or the shared movement vector to a focused entity,
 * removing the need to rely on global broadcasts.
 */
class InputRouter {
    private val actionTargets = mutableMapOf<Action, Int>()
    var movementTarget: Int? = null
        private set

    fun routeAction(action: Action, entityId: Int) {
        actionTargets[action] = entityId
    }

    fun clearAction(action: Action) {
        actionTargets.remove(action)
    }

    fun routeMovement(entityId: Int?) {
        movementTarget = entityId
    }

    internal fun resolve(action: Action): Int? = actionTargets[action]
}
