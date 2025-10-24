package com.kollider.engine.assets

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

fun <T> AssetHandle<T>.onReady(scope: CoroutineScope, block: suspend (T) -> Unit) {
    scope.launch {
        runCatching { awaitReady() }
            .onSuccess { block(it) }
    }
}
