package com.kollider.engine.core

/**
 * Represents a game.
 * Game-specific initialization (such as adding entities) can be done here.
 *
 * The [gameContext] property is platform-specific.
 */
abstract class Game(private val gameContext: GameContext)