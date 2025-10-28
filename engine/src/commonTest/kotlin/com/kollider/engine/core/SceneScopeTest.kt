package com.kollider.engine.core

import com.kollider.engine.core.storage.KeyValueStorage
import com.kollider.engine.ecs.System
import com.kollider.engine.ecs.World
import com.kollider.engine.ecs.physics.Position
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class SceneScopeTest {

    private val storage = InMemoryStorage()

    @Test
    fun `dispose removes tracked entities and systems`() {
        val world = World()
        val config = GameConfig()
        val engine = GameEngine(world, CoroutineScope(SupervisorJob() + Dispatchers.Unconfined))
        val context = GameContext(config, world, engine, storage)
        val scope = SceneScope(context, world)

        val system = TrackingSystem()
        scope.addSystem(system)

        scope.createEntity { add(Position(1f, 2f)) }

        // Ensure entity exists before dispose
        assertTrue(world.view(Position::class).iterator().hasNext())

        scope.dispose()
        // flush pending removals so onDetach is called
        world.update(0f)

        // system should have been detached
        assertTrue(system.detached)

        // no entities with Position should remain
        assertFalse(world.view(Position::class).iterator().hasNext())
    }

    private class TrackingSystem : System() {
        var detached = false
        override fun update(entities: List<com.kollider.engine.ecs.Entity>, deltaTime: Float) {}
        override fun onDetach(world: World) {
            detached = true
        }
    }

    private class InMemoryStorage : KeyValueStorage {
        private val strings = mutableMapOf<String, String>()
        private val ints = mutableMapOf<String, Int>()
        private val floats = mutableMapOf<String, Float>()
        private val bools = mutableMapOf<String, Boolean>()

        override suspend fun putString(key: String, value: String) { strings[key] = value }
        override suspend fun getString(key: String, default: String?): String? = strings[key] ?: default

        override suspend fun putInt(key: String, value: Int) { ints[key] = value }
        override suspend fun getInt(key: String, default: Int): Int = ints[key] ?: default

        override suspend fun putFloat(key: String, value: Float) { floats[key] = value }
        override suspend fun getFloat(key: String, default: Float): Float = floats[key] ?: default

        override suspend fun putBoolean(key: String, value: Boolean) { bools[key] = value }
        override suspend fun getBoolean(key: String, default: Boolean): Boolean = bools[key] ?: default

        override suspend fun remove(key: String) {
            strings.remove(key)
            ints.remove(key)
            floats.remove(key)
            bools.remove(key)
        }

        override suspend fun clear() {
            strings.clear(); ints.clear(); floats.clear(); bools.clear()
        }
    }
}
