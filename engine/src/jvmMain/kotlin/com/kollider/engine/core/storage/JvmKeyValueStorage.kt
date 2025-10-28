package com.kollider.engine.core.storage

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.prefs.Preferences

internal class JvmKeyValueStorage : KeyValueStorage {
    private val prefs: Preferences = Preferences.userRoot().node("kollider").node("storage")

    override suspend fun putString(key: String, value: String) = withContext(Dispatchers.IO) {
        prefs.put(key, value)
    }

    override suspend fun getString(key: String, default: String?): String? =
        withContext(Dispatchers.IO) { prefs.get(key, default) }

    override suspend fun putInt(key: String, value: Int) = withContext(Dispatchers.IO) {
        prefs.putInt(key, value)
    }

    override suspend fun getInt(key: String, default: Int): Int =
        withContext(Dispatchers.IO) { prefs.getInt(key, default) }

    override suspend fun putFloat(key: String, value: Float) = withContext(Dispatchers.IO) {
        prefs.putFloat(key, value)
    }

    override suspend fun getFloat(key: String, default: Float): Float =
        withContext(Dispatchers.IO) { prefs.getFloat(key, default) }

    override suspend fun putBoolean(key: String, value: Boolean) = withContext(Dispatchers.IO) {
        prefs.putBoolean(key, value)
    }

    override suspend fun getBoolean(key: String, default: Boolean): Boolean =
        withContext(Dispatchers.IO) { prefs.getBoolean(key, default) }

    override suspend fun remove(key: String) = withContext(Dispatchers.IO) {
        prefs.remove(key)
    }

    override suspend fun clear() = withContext(Dispatchers.IO) {
        prefs.clear()
    }
}
