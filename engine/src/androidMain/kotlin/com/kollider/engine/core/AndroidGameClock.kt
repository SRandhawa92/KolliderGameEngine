package com.kollider.engine.core

actual fun createGameClock(): GameClock {
    return AndroidGameClock()
}
class AndroidGameClock: GameClock {
    override fun getNanoTime(): Long {
        return System.nanoTime()
    }
}