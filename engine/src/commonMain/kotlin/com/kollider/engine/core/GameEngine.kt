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
 * The GameEngine is the main entry point for the game.
 * It manages the game loop and the ECS world.
 *
 * @param world The ECS world containing entities and systems.
 * @param scope The coroutine scope to use for the game loop.
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

    val currentScene: Scene?
        get() = synchronized(sceneLock) { sceneStack.lastOrNull()?.scene }

    fun attachContext(context: GameContext) {
        this.context = context
        flushSceneOps()
    }

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
     * Pauses the game.
     */
    fun pause() {
        world.pause()
    }

    /**
     * Resumes the game.
     */
    fun resume() {
        world.resume()
    }

    /**
     * Stops the game.
     */
    fun stop() {
        running = false
        gameJob?.cancel()
        clearScenesImmediate()
        world.dispose()
    }

    /**
     * Resizes the game window.
     */
    fun resize(width: Int, height: Int) {
        world.resize(width, height)
    }

    fun pushScene(scene: Scene) {
        enqueueSceneOp(SceneOp.Push(scene))
    }

    fun popScene() {
        enqueueSceneOp(SceneOp.Pop)
    }

    fun replaceScene(scene: Scene) {
        enqueueSceneOp(SceneOp.Replace(scene))
    }

    fun clearScenes() {
        enqueueSceneOp(SceneOp.Clear)
    }

    private fun enqueueSceneOp(op: SceneOp) {
        synchronized(sceneLock) {
            pendingSceneOps.addLast(op)
        }
    }

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

    private fun currentSceneEntry(): SceneEntry? = synchronized(sceneLock) { sceneStack.lastOrNull() }

    private sealed interface SceneOp {
        data class Push(val scene: Scene) : SceneOp
        object Pop : SceneOp
        data class Replace(val scene: Scene) : SceneOp
        object Clear : SceneOp
    }
}
