package com.kollider.engine.core.storage

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.Foundation.NSUserDefaults

private val defaults = NSUserDefaults.standardUserDefaults()

internal class IosKeyValueStorage : KeyValueStorage {
    override suspend fun putString(key: String, value: String) = withContext(Dispatchers.Main) {
        defaults.setObject(value, forKey = key)
    }

    override suspend fun getString(key: String, default: String?): String? =
        withContext(Dispatchers.Main) { defaults.stringForKey(key) ?: default }

    override suspend fun putInt(key: String, value: Int) = withContext(Dispatchers.Main) {
        defaults.setInteger(value.toLong(), forKey = key)
    }

    override suspend fun getInt(key: String, default: Int): Int =
        withContext(Dispatchers.Main) { defaults.integerForKey(key).toInt() }

    override suspend fun putFloat(key: String, value: Float) = withContext(Dispatchers.Main) {
        defaults.setFloat(value, forKey = key)
    }

    override suspend fun getFloat(key: String, default: Float): Float =
        withContext(Dispatchers.Main) { defaults.floatForKey(key) }

    override suspend fun putBoolean(key: String, value: Boolean) = withContext(Dispatchers.Main) {
        defaults.setBool(value, forKey = key)
    }

    override suspend fun getBoolean(key: String, default: Boolean): Boolean =
        withContext(Dispatchers.Main) { defaults.boolForKey(key) }

    override suspend fun remove(key: String) = withContext(Dispatchers.Main) {
        defaults.removeObjectForKey(key)
    }

    override suspend fun clear() = withContext(Dispatchers.Main) {
        defaults.dictionaryRepresentation().keys.forEach { key ->
            defaults.removeObjectForKey(key as String)
        }
    }
}
