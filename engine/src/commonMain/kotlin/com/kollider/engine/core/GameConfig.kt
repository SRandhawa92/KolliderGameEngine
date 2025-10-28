package com.kollider.engine.core

import com.kollider.engine.assets.AssetManager
import com.kollider.engine.core.storage.KeyValueStorage
import com.kollider.engine.ecs.input.InputRouter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * Mutable configuration used to initialise a game session.
 *
 * Modify the properties inside the builder returned by [createKolliderGame].
 *
 * ```kotlin
 * createKolliderGame {
 *     title = "Sample Game"
 *     width = 1280
 *     height = 720
 * }
 * ```
 */
data class GameConfig(
    var width: Int = 800,
    var height: Int = 600,
    var title: String = "Kollider Game",
    var appContext: AppContext = AppContext,
    var scope: CoroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob()),
    val assets: AssetManager = AssetManager(scope),
    val inputRouter: InputRouter = InputRouter(),
    var renderWidthOverride: Int? = null,
    var renderHeightOverride: Int? = null,
    var storageFactory: ((GameConfig) -> KeyValueStorage)? = null,
) {
    /**
     * Width in pixels used by the renderer. Falls back to [width] when no override is provided.
     */
    val renderWidth: Int
        get() = renderWidthOverride ?: width

    /**
     * Height in pixels used by the renderer. Falls back to [height] when no override is provided.
     */
    val renderHeight: Int
        get() = renderHeightOverride ?: height

    /**
     * Convenience property that exposes the playable area's bounds.
     *
     * ```kotlin
     * val bounds = config.worldBounds
     * ```
     */
    val worldBounds: WorldBounds
        get() = WorldBounds(
            width = width.toFloat(),
            height = height.toFloat(),
        )
}
