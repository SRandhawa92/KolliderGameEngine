package com.kollider.engine.ecs

/**
 * Base class for logic that operates on collections of [Entity] instances.
 *
 * Systems are attached to a [World] and receive lifecycle callbacks mirroring the game loop.
 * Override [update] to implement behaviour, and optionally [resize], [pause], [resume], or
 * [dispose] for resource management.
 *
 * ```kotlin
 * class GravitySystem : System() {
 *     override fun update(entities: List<Entity>, deltaTime: Float) {
 *         entities.filter { it.has(Velocity::class) }.forEach { entity ->
 *             entity.require<Velocity>().vy += 9.81f * deltaTime
 *         }
 *     }
 * }
 * ```
 */
abstract class System {
    private var worldRef: World? = null

    /**
     * Shortcut to the [World] the system is bound to. Throws if accessed before [onAttach].
     */
    protected open val world: World
        get() = worldRef ?: error("System is not attached to a World.")

    /**
     * Called by [World] when the system is first added.
     */
    internal fun bindWorld(world: World) {
        worldRef = world
        onAttach(world)
    }

    /**
     * Called when the system is removed from the world or the world is disposed.
     */
    internal fun unbindWorld() {
        val current = worldRef ?: return
        onDetach(current)
        worldRef = null
    }

    /**
     * Lifecycle hook invoked after the system is attached to a [World].
     *
     * Use this to create cached [EntityView] references or initialise state.
     */
    protected open fun onAttach(world: World) {}

    /**
     * Lifecycle hook invoked before the system is detached.
     *
     * Use this to release references or cancel coroutines.
     */
    protected open fun onDetach(world: World) {}

    /**
     * Updates the system.
     *
     * @param entities the list of entities.
     * @param deltaTime time elapsed since the last update.
     */
    abstract fun update(entities: List<Entity>, deltaTime: Float)

    /**
     * Called when the system is no longer needed.
     *
     * Override to dispose resources such as render targets or open files.
     */
    open fun dispose() {}

    /**
     * Called when the window is resized.
     *
     * The default implementation does nothing; override if your system depends on the window size.
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
