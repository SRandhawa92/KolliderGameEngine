package com.kollider.flappybird

/**
 * Lightweight state holder shared between Flappy Bird systems so they can coordinate
 * game-over and restart behaviour while demonstrating engine-driven scene flow.
 */
class FlappyBirdGameState(
    val restartDelaySeconds: Float = 1.5f,
    val onGameOver: (() -> Unit)? = null,
    val onRestart: (() -> Unit)? = null,
) {
    var isRunning: Boolean = true
        private set

    internal var restartTimer: Float = 0f
        private set

    fun enterGameOver() {
        if (!isRunning) return
        isRunning = false
        restartTimer = 0f
        onGameOver?.invoke()
    }

    fun tickRestart(delta: Float): Boolean {
        if (isRunning) return false
        restartTimer += delta
        return restartTimer >= restartDelaySeconds
    }

    fun markRestarted() {
        isRunning = true
        restartTimer = 0f
        onRestart?.invoke()
    }
}
