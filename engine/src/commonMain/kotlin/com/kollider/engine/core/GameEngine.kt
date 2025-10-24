package com.kollider.engine.core

import com.kollider.engine.ecs.World
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.internal.SynchronizedObject
import kotlinx.coroutines.internal.synchronized
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.collections.ArrayDeque
import kotlin.collections.buildList

/**
 * Coordinates the main game loop, scene stack, and ECS world updates.
 *
 * Typically created by [KolliderGameBuilder]. You rarely need to instantiate this manually.
 */
@Suppress("UNUSED")
@OptIn(InternalCoroutinesApi::class)
class GameEngine(
    private val world: World,
    private val scope: CoroutineScope,
) {
    private var running = true
    private val clock = createGameClock()
    private var gameJob: Job? = null
    private var context: GameContext? = null
    private data class SceneEntry(val scene: Scene, val scope: SceneScope)

    private val sceneStack = ArrayDeque<SceneEntry>()
    private val pendingSceneOps = ArrayDeque<SceneOp>()
    private val sceneLock = SynchronizedObject()

    /**
     * Currently active scene at the top of the stack, or `null` if none are active.
     *
     * ```kotlin
     * val scene = engine.currentScene
     * ```
     */
    val currentScene: Scene?
        get() = synchronized(sceneLock) { sceneStack.lastOrNull()?.scene }

    /**
     * Connects the engine with the surrounding [GameContext]. Must be invoked before calling [start].
     *
     * ```kotlin
     * engine.attachContext(context)
     * ```
     */
    fun attachContext(context: GameContext) {
        this.context = context
        flushSceneOps()
    }

    /**
     * Starts the main loop as a coroutine on the supplied [scope].
     *
     * ```kotlin
     * engine.start()
     * ```
     */
    fun start() {
        gameJob = scope.launch {
            var lastTime = clock.getNanoTime()
            while (isActive && running) {
                ensureActive()
                val now = clock.getNanoTime()
                val deltaTime = (now - lastTime) / 1_000_000_000f
                lastTime = now

                flushSceneOps()
                currentSceneEntry()?.let { (scene, scope) ->
                    scene.onUpdate(deltaTime, scope)
                }
                // Update game objects via the ECS.
                world.update(deltaTime)

                // Small delay to allow coroutine to cancel.
                delay(1)

                // Frame rate control logic could be inserted here.
            }
        }
    }


    /**
     * Pauses all systems. The main loop continues running.
     *
     * ```kotlin
     * engine.pause()
     * ```
     */
    fun pause() {
        world.pause()
    }

    /**
     * Resumes all systems after a pause.
     *
     * ```kotlin
     * engine.resume()
     * ```
     */
    fun resume() {
        world.resume()
    }

    /**
     * Stops the loop, disposes the world, and clears any active scenes.
     *
     * ```kotlin
     * engine.stop()
     * ```
     */
    fun stop() {
        running = false
        gameJob?.cancel()
        clearScenesImmediate()
        world.dispose()
    }

    /**
     * Forwards a resize event to all systems.
     *
     * ```kotlin
     * engine.resize(width, height)
     * ```
     */
    fun resize(width: Int, height: Int) {
        world.resize(width, height)
    }

    /**
     * Pushes a new [scene] on top of the stack, pausing the current one if present.
     *
     * ```kotlin
     * engine.pushScene(MainMenuScene())
     * ```
     */
    fun pushScene(scene: Scene) {
        enqueueSceneOp(SceneOp.Push(scene))
    }

    /**
     * Pops the current scene, resuming the previous one if available.
     *
     * ```kotlin
     * engine.popScene()
     * ```
     */
    fun popScene() {
        enqueueSceneOp(SceneOp.Pop)
    }

    /**
     * Replaces the current scene with [scene] in a single operation.
     *
     * ```kotlin
     * engine.replaceScene(GameplayScene())
     * ```
     */
    fun replaceScene(scene: Scene) {
        enqueueSceneOp(SceneOp.Replace(scene))
    }

    /**
     * Clears the entire scene stack.
     *
     * ```kotlin
     * engine.clearScenes()
     * ```
     */
    fun clearScenes() {
        enqueueSceneOp(SceneOp.Clear)
    }

    /**
     * Adds a scene operation to the queue. Executed on the next loop iteration.
     */
    private fun enqueueSceneOp(op: SceneOp) {
        synchronized(sceneLock) {
            pendingSceneOps.addLast(op)
        }
    }

    /**
     * Drains queued scene operations while holding the lock to maintain order.
     */
    private fun drainPendingSceneOps(): List<SceneOp> {
        synchronized(sceneLock) {
            if (pendingSceneOps.isEmpty()) return emptyList()
            return buildList {
                while (pendingSceneOps.isNotEmpty()) {
                    add(pendingSceneOps.removeFirst())
                }
            }
        }
    }

    /**
     * Executes queued scene operations, invoking lifecycle hooks as needed.
     */
    private fun flushSceneOps() {
        val ctx = context ?: return
        val ops = drainPendingSceneOps()
        if (ops.isEmpty()) return
        ops.forEach { op ->
            when (op) {
                is SceneOp.Push -> {
                    val scope = SceneScope(ctx, world)
                    sceneStack.addLast(SceneEntry(op.scene, scope))
                    op.scene.onEnter(scope)
                }
                SceneOp.Pop -> {
                    sceneStack.removeLastOrNull()?.let { entry ->
                        entry.scene.onExit(entry.scope)
                        entry.scope.dispose()
                    }
                }
                is SceneOp.Replace -> {
                    sceneStack.removeLastOrNull()?.let { entry ->
                        entry.scene.onExit(entry.scope)
                        entry.scope.dispose()
                    }
                    val scope = SceneScope(ctx, world)
                    sceneStack.addLast(SceneEntry(op.scene, scope))
                    op.scene.onEnter(scope)
                }
                SceneOp.Clear -> {
                    while (sceneStack.isNotEmpty()) {
                        val entry = sceneStack.removeLast()
                        entry.scene.onExit(entry.scope)
                        entry.scope.dispose()
                    }
                }
            }
        }
    }

    /**
     * Immediately clears the scene stack and associated scopes.
     */
    private fun clearScenesImmediate() {
        synchronized(sceneLock) {
            pendingSceneOps.clear()
            while (sceneStack.isNotEmpty()) {
                val entry = sceneStack.removeLast()
                entry.scene.onExit(entry.scope)
                entry.scope.dispose()
            }
        }
    }

    /**
     * Safe helper for reading the current scene entry while holding the lock.
     */
    private fun currentSceneEntry(): SceneEntry? = synchronized(sceneLock) { sceneStack.lastOrNull() }

    private sealed interface SceneOp {
        data class Push(val scene: Scene) : SceneOp
        object Pop : SceneOp
        data class Replace(val scene: Scene) : SceneOp
        object Clear : SceneOp
    }
}
