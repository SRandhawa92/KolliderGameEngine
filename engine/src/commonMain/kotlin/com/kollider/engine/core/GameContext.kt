package com.kollider.engine.core

import com.kollider.engine.core.storage.KeyValueStorage
import com.kollider.engine.ecs.World

/**
 * Aggregates engine services required by running games and scenes.
 *
 * ```kotlin
 * fun GameContext.spawnPlayer() {
 *     world.createEntity().apply {
 *         add(Position(0f, 0f))
 *     }
 * }
 * ```
 */
data class GameContext(
    val config: GameConfig,
    val world: World,
    val engine: GameEngine,
    val storage: KeyValueStorage,
)
