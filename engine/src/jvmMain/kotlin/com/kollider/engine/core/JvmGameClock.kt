package com.kollider.engine.core

actual fun createGameClock(): GameClock {
    return JvmGameClock()
}

class JvmGameClock : GameClock {
    override fun getNanoTime(): Long {
        return System.nanoTime()
    }
}