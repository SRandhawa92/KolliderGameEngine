package com.kollider.engine.core

/**
 * Creates a high-resolution platform clock used by the game loop.
 */
expect fun createGameClock(): GameClock

/**
 * Provides an API for retrieving monotonic timestamps.
 */
interface GameClock {
    /**
     * @return Current time in nanoseconds since an arbitrary origin.
     */
    fun getNanoTime(): Long
}
