package com.kollider.engine.core

import com.kollider.engine.ecs.Entity
import com.kollider.engine.ecs.System
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class CountingSystem : System() {
    var updateCount = 0
    override fun update(entities: List<Entity>, deltaTime: Float) {
        println("CountingSystem update")
        updateCount++
    }
}

class DummyGame(context: GameContext) : Game(context) {
    companion object {
        var started = false
    }
    init {
        started = true
    }
}

class KolliderGameBuilderTest {

    @Test
    fun `builder starts dummy game and systems update`() = runTest {
        // Reset dummy game flag.
        DummyGame.started = false

        // Create a CountingSystem to check if systems update.
        val countingSystem = CountingSystem()

        // Create a game config. Ensure the config has a mutable scope property.
        // For this test, we'll assume GameConfig has a 'scope' property.
        val configInitializer: GameConfig.() -> Unit = {
            width = 800
            height = 600
            title = "Dummy Game"
            scope = this@runTest.backgroundScope
        }

        createKolliderGame(configInitializer)
            .entities {
                entity { createEntity() }
            }
            .systems {
                system { countingSystem }
            }
            .start { context ->
                DummyGame(context)
            }

        // Let the game loop run briefly.
        delay(100)

        // The test scope will cancel the engine automatically when the test completes.
        assertTrue(DummyGame.started, "DummyGame should have been started")
        assertTrue(countingSystem.updateCount > 0, "CountingSystem should have been updated")
    }
}
