package com.kollider.engine.core.storage

/**
 * Minimal key-value storage abstraction for persisting lightweight data such as high scores.
 * Platform implementations should be thread-safe and survive app restarts.
 */
interface KeyValueStorage {
    suspend fun putString(key: String, value: String)
    suspend fun getString(key: String, default: String? = null): String?

    suspend fun putInt(key: String, value: Int)
    suspend fun getInt(key: String, default: Int = 0): Int

    suspend fun putFloat(key: String, value: Float)
    suspend fun getFloat(key: String, default: Float = 0f): Float

    suspend fun putBoolean(key: String, value: Boolean)
    suspend fun getBoolean(key: String, default: Boolean = false): Boolean

    suspend fun remove(key: String)
    suspend fun clear()
}
