package com.kollider.engine.core.storage

import com.kollider.engine.core.GameConfig

internal fun jsStorageFactory(@Suppress("UNUSED_PARAMETER") config: GameConfig): KeyValueStorage {
    return JsKeyValueStorage()
}

internal actual fun platformStorageFactory(): (GameConfig) -> KeyValueStorage = ::jsStorageFactory
