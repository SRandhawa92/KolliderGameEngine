package com.kollider.engine.core

actual fun createGameClock(): GameClock {
    return JsGameClock()
}

class JsGameClock : GameClock {
    override fun getNanoTime(): Long {
        return (js("performance.now()") as Double * 1_000_000).toLong()
    }
}