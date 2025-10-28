package com.kollider.engine.core.storage

import com.kollider.engine.core.KolliderGameBuilder

@Suppress("unused")
private val initStorage = run {
    KolliderGameBuilder.defaultStorageFactory = ::iosStorageFactory
}
