package com.kollider.engine.ecs

import kotlin.test.Test
import kotlin.test.assertEquals

class WorldPauseTest {

    @Test
    fun `systems stop updating when paused unless opting in`() {
        val world = World()
        val pausedSystem = CountingSystem()
        val keepAliveSystem = object : CountingSystem() {
            override val runsWhilePaused: Boolean get() = true
        }

        world.addSystem(pausedSystem)
        world.addSystem(keepAliveSystem)

        world.update(0f)
        assertEquals(1, pausedSystem.ticks)
        assertEquals(1, keepAliveSystem.ticks)

        world.pause()
        world.update(0f)

        assertEquals(1, pausedSystem.ticks, "Paused system should not tick while paused")
        assertEquals(2, keepAliveSystem.ticks, "runsWhilePaused system should continue ticking")

        world.resume()
        world.update(0f)
        assertEquals(2, pausedSystem.ticks)
        assertEquals(3, keepAliveSystem.ticks)
    }

    private open class CountingSystem : System() {
        var ticks = 0
        override fun update(entities: List<Entity>, deltaTime: Float) {
            ticks += 1
        }
    }
}
