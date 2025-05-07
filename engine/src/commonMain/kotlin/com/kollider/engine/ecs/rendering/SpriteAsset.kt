package com.kollider.engine.ecs.rendering

/**
 * Represents a 2D spriteAsset used by the Renderer.
 * The [image] property is platform-specific.
 */
abstract class SpriteAsset {
    abstract val name: String
    abstract val image: Any
}