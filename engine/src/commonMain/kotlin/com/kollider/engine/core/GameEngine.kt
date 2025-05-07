package com.kollider.engine.core

import com.kollider.engine.ecs.World
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * The GameEngine is the main entry point for the game.
 * It manages the game loop and the ECS world.
 *
 * @param world The ECS world containing entities and systems.
 * @param scope The coroutine scope to use for the game loop.
 */
@Suppress("UNUSED")
class GameEngine(
    private val world: World,
    private val scope: CoroutineScope,
) {
    private var running = true
    private val clock = createGameClock()
    private var gameJob: Job? = null

    fun start() {
        gameJob = scope.launch {
            var lastTime = clock.getNanoTime()
            while (isActive && running) {
                ensureActive()
                val now = clock.getNanoTime()
                val deltaTime = (now - lastTime) / 1_000_000_000f
                lastTime = now

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
        world.dispose()
    }

    /**
     * Resizes the game window.
     */
    fun resize(width: Int, height: Int) {
        world.resize(width, height)
    }
}
