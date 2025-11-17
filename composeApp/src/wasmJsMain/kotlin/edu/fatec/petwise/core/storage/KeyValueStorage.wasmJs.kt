package edu.fatec.petwise.core.storage

import kotlinx.browser.localStorage

actual object KeyValueStorage {
    actual fun putString(key: String, value: String) {
        localStorage.setItem(key, value)
    }

    actual fun getString(key: String): String? {
        return localStorage.getItem(key)
    }

    actual fun putLong(key: String, value: Long) {
        localStorage.setItem(key, value.toString())
    }

    actual fun getLong(key: String): Long? {
        return localStorage.getItem(key)?.toLongOrNull()
    }

    actual fun remove(key: String) {
        localStorage.removeItem(key)
    }

    actual fun clear() {
        localStorage.clear()
    }
}