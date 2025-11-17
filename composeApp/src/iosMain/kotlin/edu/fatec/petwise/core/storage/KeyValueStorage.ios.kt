package edu.fatec.petwise.core.storage

import platform.Foundation.NSUserDefaults

actual object KeyValueStorage {
    private val defaults = NSUserDefaults.standardUserDefaults

    actual fun putString(key: String, value: String) {
        defaults.setObject(value, key)
    }

    actual fun getString(key: String): String? {
        return defaults.stringForKey(key)
    }

    actual fun putLong(key: String, value: Long) {
        defaults.setInteger(value.toLong(), key)
    }

    actual fun getLong(key: String): Long? {
        val value = defaults.integerForKey(key)
        return if (value == 0L && !defaults.objectForKey(key)?.let { true } ?: false) null else value
    }

    actual fun remove(key: String) {
        defaults.removeObjectForKey(key)
    }

    actual fun clear() {
        defaults.removePersistentDomainForName(NSUserDefaults.standardUserDefaults.persistentDomainNames.firstOrNull() as? String ?: "")
    }
}