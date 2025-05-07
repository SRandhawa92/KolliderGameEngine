package com.kollider.engine.core

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import platform.posix.CLOCK_MONOTONIC
import platform.posix.clock_gettime
import platform.posix.timespec

actual fun createGameClock(): GameClock {
    return IosGameClock()
}

@OptIn(ExperimentalForeignApi::class)
class IosGameClock : GameClock {
    override fun getNanoTime(): Long {
        return memScoped {
            val timespec = alloc<timespec>()
            clock_gettime(CLOCK_MONOTONIC.toUInt(), timespec.ptr)
            (timespec.tv_sec * 1_000_000_000) + timespec.tv_nsec
        }
    }
}