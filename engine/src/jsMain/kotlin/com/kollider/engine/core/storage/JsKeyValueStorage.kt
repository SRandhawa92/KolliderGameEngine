package com.kollider.engine.core.storage

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private val storage = kotlinx.browser.window.localStorage

internal class JsKeyValueStorage : KeyValueStorage {
    override suspend fun putString(key: String, value: String) = withContext(Dispatchers.Default) {
        storage.setItem(key, value)
    }

    override suspend fun getString(key: String, default: String?): String? =
        withContext(Dispatchers.Default) { storage.getItem(key) ?: default }

    override suspend fun putInt(key: String, value: Int) = putString(key, value.toString())

    override suspend fun getInt(key: String, default: Int): Int =
        getString(key)?.toIntOrNull() ?: default

    override suspend fun putFloat(key: String, value: Float) = putString(key, value.toString())

    override suspend fun getFloat(key: String, default: Float): Float =
        getString(key)?.toFloatOrNull() ?: default

    override suspend fun putBoolean(key: String, value: Boolean) = putString(key, value.toString())

    override suspend fun getBoolean(key: String, default: Boolean): Boolean =
        getString(key)?.toBooleanStrictOrNull() ?: default

    override suspend fun remove(key: String) = withContext(Dispatchers.Default) {
        storage.removeItem(key)
    }

    override suspend fun clear() = withContext(Dispatchers.Default) {
        storage.clear()
    }
}
