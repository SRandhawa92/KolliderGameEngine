package com.kollider.engine.ecs

/**
 * Marker base class for all ECS components.
 *
 * Components store pure data and are attached to [Entity] instances. Keep components
 * lightweight and immutable where possible to simplify testing.
 *
 * ```kotlin
 * data class Health(var current: Int, var max: Int) : Component()
 * entity.add(Health(current = 100, max = 100))
 * ```
 */
open class Component
