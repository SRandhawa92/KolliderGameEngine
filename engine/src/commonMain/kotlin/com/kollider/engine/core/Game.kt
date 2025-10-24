package com.kollider.engine.core

/**
 * Base class for game entry points created via [createKolliderGame].
 *
 * Subclass this to bootstrap your world with entities or additional systems.
 *
 * ```kotlin
 * class MyGame(context: GameContext) : Game(context) {
 *     init {
 *         context.world.createEntity()
 *     }
 * }
 * ```
 */
abstract class Game(private val gameContext: GameContext)
