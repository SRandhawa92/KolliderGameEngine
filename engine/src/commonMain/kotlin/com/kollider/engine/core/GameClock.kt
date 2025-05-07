package com.kollider.engine.core

expect fun createGameClock(): GameClock

interface GameClock {
    fun getNanoTime(): Long
}