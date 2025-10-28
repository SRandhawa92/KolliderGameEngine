package com.kollider.engine.core.storage

import com.kollider.engine.core.GameConfig

internal expect fun platformStorageFactory(): (GameConfig) -> KeyValueStorage
