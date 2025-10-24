package com.kollider.engine.core

/**
 * Lightweight scene abstraction managed by [GameEngine]. Scenes can perform their own logic
 * during [onUpdate], and are notified when they enter or leave the stack.
 */
interface Scene {
    fun onEnter(context: GameContext) {}
    fun onExit() {}
    fun onUpdate(deltaTime: Float) {}
}
