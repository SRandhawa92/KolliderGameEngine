package com.kollider.engine.core

import com.kollider.engine.ecs.World

/**
 * Represents the context of a game.
 * Contains the game configuration and the game world.
 */
data class GameContext(
    val config: GameConfig,
    val world: World,
    val engine: GameEngine
)