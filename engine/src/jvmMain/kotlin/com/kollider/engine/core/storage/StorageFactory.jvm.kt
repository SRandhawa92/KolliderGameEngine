package com.kollider.engine.core.storage

import com.kollider.engine.core.GameConfig

internal fun jvmStorageFactory(@Suppress("UNUSED_PARAMETER") config: GameConfig): KeyValueStorage {
    return JvmKeyValueStorage()
}
