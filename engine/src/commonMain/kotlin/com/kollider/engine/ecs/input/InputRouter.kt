package com.kollider.engine.ecs.input

/**
 * Utility that directs input from the shared handler to focused entities.
 *
 * Useful for menu navigation or local multiplayer, where only a subset of entities should
 * receive certain actions.
 *
 * ```kotlin
 * config.inputRouter.routeAction(Shoot, playerEntityId)
 * config.inputRouter.routeMovement(playerEntityId)
 * ```
 */
class InputRouter {
    private val actionTargets = mutableMapOf<Action, Int>()
    var movementTarget: Int? = null
        private set

    /**
     * Routes the provided [action] to the entity with [entityId].
     *
     * ```kotlin
     * router.routeAction(Shoot, playerId)
     * ```
     */
    fun routeAction(action: Action, entityId: Int) {
        actionTargets[action] = entityId
    }

    /**
     * Removes a previously assigned action routing.
     *
     * ```kotlin
     * router.clearAction(Shoot)
     * ```
     */
    fun clearAction(action: Action) {
        actionTargets.remove(action)
    }

    /**
     * Routes analog movement to [entityId]. Pass `null` to broadcast to everyone.
     *
     * ```kotlin
     * router.routeMovement(entityId = focusedEntity.id)
     * ```
     */
    fun routeMovement(entityId: Int?) {
        movementTarget = entityId
    }

    /**
     * Resolves the routed entity for [action], or `null` if none is assigned.
     */
    internal fun resolve(action: Action): Int? = actionTargets[action]
}
