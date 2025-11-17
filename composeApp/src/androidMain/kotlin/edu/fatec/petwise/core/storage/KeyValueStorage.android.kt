package edu.fatec.petwise.core.storage

import android.content.Context
import android.content.SharedPreferences

actual object KeyValueStorage {
    private var prefs: SharedPreferences? = null

    actual fun putString(key: String, value: String) {
        prefs?.edit()?.putString(key, value)?.apply()
    }

    actual fun getString(key: String): String? {
        return prefs?.getString(key, null)
    }

    actual fun putLong(key: String, value: Long) {
        prefs?.edit()?.putLong(key, value)?.apply()
    }

    actual fun getLong(key: String): Long? {
        val value = prefs?.getLong(key, -1L) ?: -1L
        return if (value == -1L) null else value
    }

    actual fun remove(key: String) {
        prefs?.edit()?.remove(key)?.apply()
    }

    actual fun clear() {
        prefs?.edit()?.clear()?.apply()
    }

    fun init(context: Context) {
        if (prefs == null) {
            prefs = context.getSharedPreferences("PetWisePrefs", Context.MODE_PRIVATE)
        }
    }
}