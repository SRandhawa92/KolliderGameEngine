package com.kollider.engine.core.storage

import com.kollider.engine.core.GameConfig

internal fun iosStorageFactory(@Suppress("UNUSED_PARAMETER") config: GameConfig): KeyValueStorage {
    return IosKeyValueStorage()
}

internal actual fun platformStorageFactory(): (GameConfig) -> KeyValueStorage = ::iosStorageFactory
