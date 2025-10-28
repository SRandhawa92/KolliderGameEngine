package com.kollider.engine.core.storage

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val STORAGE_FILE = "kollider_storage"

internal class AndroidKeyValueStorage(context: Context) : KeyValueStorage {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(STORAGE_FILE, Context.MODE_PRIVATE)

    override suspend fun putString(key: String, value: String) = edit { putString(key, value) }

    override suspend fun getString(key: String, default: String?): String? =
        withContext(Dispatchers.IO) { prefs.getString(key, default) }

    override suspend fun putInt(key: String, value: Int) = edit { putInt(key, value) }

    override suspend fun getInt(key: String, default: Int): Int =
        withContext(Dispatchers.IO) { prefs.getInt(key, default) }

    override suspend fun putFloat(key: String, value: Float) = edit { putFloat(key, value) }

    override suspend fun getFloat(key: String, default: Float): Float =
        withContext(Dispatchers.IO) { prefs.getFloat(key, default) }

    override suspend fun putBoolean(key: String, value: Boolean) = edit { putBoolean(key, value) }

    override suspend fun getBoolean(key: String, default: Boolean): Boolean =
        withContext(Dispatchers.IO) { prefs.getBoolean(key, default) }

    override suspend fun remove(key: String) = edit { remove(key) }

    override suspend fun clear() = edit { clear() }

    private suspend inline fun edit(crossinline block: SharedPreferences.Editor.() -> Unit) {
        withContext(Dispatchers.IO) {
            prefs.edit().apply {
                block()
                apply()
            }
        }
    }
}
