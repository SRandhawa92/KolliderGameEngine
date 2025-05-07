package com.kollider.engine.ecs

/**
 * Base class for all systems in the ECS.
 */
abstract class System {

    /**
     * Updates the system.
     *
     * @param entities the list of entities.
     * @param deltaTime time elapsed since the last update.
     */
    abstract fun update(entities: List<Entity>, deltaTime: Float)

    /**
     * Called when the system is no longer needed.
     */
    open fun dispose() {}

    /**
     * Called when the window is resized.
     */
    open fun resize(width: Int, height: Int) {}

    /**
     * Called when the system is paused.
     */
    open fun pause() {}

    /**
     * Called when the system is resumed.
     */
    open fun resume() {}
}
