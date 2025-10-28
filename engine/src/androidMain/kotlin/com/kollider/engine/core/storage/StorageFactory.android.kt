package com.kollider.engine.core.storage

import com.kollider.engine.core.GameConfig

internal fun androidStorageFactory(config: GameConfig): KeyValueStorage {
    val context = config.appContext.get()
        ?: throw IllegalStateException("Android context not available for storage")
    return AndroidKeyValueStorage(context)
}

internal actual fun platformStorageFactory(): (GameConfig) -> KeyValueStorage = ::androidStorageFactory
