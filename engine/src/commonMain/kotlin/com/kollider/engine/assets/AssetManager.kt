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

sealed class AssetState<out T> {
    object Loading : AssetState<Nothing>()
    data class Ready<T>(val value: T) : AssetState<T>()
    data class Error(val throwable: Throwable) : AssetState<Nothing>()
}

class AssetHandle<T> internal constructor(
    private val stateFlow: MutableStateFlow<AssetState<T>>,
) {
    val state: StateFlow<AssetState<T>> = stateFlow

    val isReady: Boolean
        get() = stateFlow.value is AssetState.Ready

    fun getOrNull(): T? = (stateFlow.value as? AssetState.Ready<T>)?.value

    suspend fun awaitReady(): T {
        val completed = stateFlow.filter { it !is AssetState.Loading }.first()
        return when (completed) {
            is AssetState.Ready -> completed.value
            is AssetState.Error -> throw completed.throwable
            else -> error("Unexpected asset state: $completed")
        }
    }
}

class AssetManager(
    private val scope: CoroutineScope,
) {
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
