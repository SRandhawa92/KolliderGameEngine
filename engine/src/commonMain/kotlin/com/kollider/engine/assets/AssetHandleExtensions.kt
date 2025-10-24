package com.kollider.engine.assets

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Convenience extension that launches [block] when the handle transitions into the ready state.
 *
 * ```kotlin
 * textureHandle.onReady(scope) { texture ->
 *     renderer.cache(texture)
 * }
 * ```
 */
fun <T> AssetHandle<T>.onReady(scope: CoroutineScope, block: suspend (T) -> Unit) {
    scope.launch {
        runCatching { awaitReady() }
            .onSuccess { block(it) }
    }
}
