package com.kollider.engine.assets

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Represents the lifecycle stages of an asset loaded through [AssetManager].
 *
 * When working with a handle, always account for the asynchronous nature of loading.
 *
 * ```kotlin
 * when (val state = handle.state.value) {
 *     AssetState.Loading -> showLoadingSpinner()
 *     is AssetState.Ready -> useTexture(state.value)
 *     is AssetState.Error -> logError(state.throwable)
 * }
 * ```
 */
sealed class AssetState<out T> {
    /**
     * The asset is in the process of being loaded.
     */
    object Loading : AssetState<Nothing>()

    /**
     * The asset finished loading successfully and exposes its [value].
     */
    data class Ready<T>(val value: T) : AssetState<T>()

    /**
     * The asset failed to load; the cause is stored in [throwable].
     */
    data class Error(val throwable: Throwable) : AssetState<Nothing>()
}

/**
 * Represents a type-safe handle returned by [AssetManager.load].
 *
 * The handle keeps track of the current [AssetState] and exposes convenience helpers
 * for polling or awaiting its completion.
 *
 * ```kotlin
 * val textureHandle = assets.load("texture.png") { loadTexture("texture.png") }
 * if (textureHandle.isReady) {
 *     draw(textureHandle.getOrNull()!!)
 * }
 * ```
 */
class AssetHandle<T> internal constructor(
    private val stateFlow: MutableStateFlow<AssetState<T>>,
) {
    /**
     * Reactive access to the handle state, intended for UI bindings or coroutines.
     *
     * ```kotlin
     * scope.launch {
     *     handle.state.collect { /* render progress */ }
     * }
     * ```
     */
    val state: StateFlow<AssetState<T>> = stateFlow

    /**
     * `true` when the underlying asset has completed loading successfully.
     *
     * ```kotlin
     * if (handle.isReady) use(handle.getOrNull())
     * ```
     */
    val isReady: Boolean
        get() = stateFlow.value is AssetState.Ready

    /**
     * Returns the loaded asset or `null` if the asset is not ready or failed to load.
     *
     * ```kotlin
     * textureHandle.getOrNull()?.let { texture ->
     *     renderer.draw(texture)
     * }
     * ```
     */
    fun getOrNull(): T? = (stateFlow.value as? AssetState.Ready<T>)?.value

    /**
     * Suspends until the asset finishes loading.
     *
     * @throws Throwable propagates the error if the asset fails to load.
     *
     * ```kotlin
     * val sound = soundHandle.awaitReady()
     * sound.play()
     * ```
     */
    suspend fun awaitReady(): T {
        val completed = stateFlow.filter { it !is AssetState.Loading }.first()
        return when (completed) {
            is AssetState.Ready -> completed.value
            is AssetState.Error -> throw completed.throwable
            else -> error("Unexpected asset state: $completed")
        }
    }
}

/**
 * Coordinates asynchronous loading of assets that can be reused across systems and scenes.
 *
 * Each call to [load] returns an [AssetHandle] that can be observed for completion. The
 * manager centralises error handling and ensures loads run on a background dispatcher.
 *
 * ```kotlin
 * val textureHandle = assetManager.load("player") { loadTextureResource("player.png") }
 * scope.launch {
 *     val texture = textureHandle.awaitReady()
 *     sprites.add(texture)
 * }
 * ```
 */
class AssetManager(
    private val scope: CoroutineScope,
) {
    /**
     * Starts loading an asset and returns a handle that can be observed.
     *
     * @param name Logical identifier for debugging or caching purposes.
     * @param dispatcher Coroutine dispatcher used for the [loader] work.
     * @param loader Suspended block that performs the actual load.
     */
    fun <T> load(
        name: String,
        dispatcher: CoroutineDispatcher = Dispatchers.Default,
        loader: suspend () -> T,
    ): AssetHandle<T> {
        val state = MutableStateFlow<AssetState<T>>(AssetState.Loading)
        val handle = AssetHandle(state)
        scope.launch {
            try {
                val value = withContext(dispatcher) { loader() }
                state.value = AssetState.Ready(value)
            } catch (t: Throwable) {
                state.value = AssetState.Error(t)
            }
        }
        return handle
    }
}
