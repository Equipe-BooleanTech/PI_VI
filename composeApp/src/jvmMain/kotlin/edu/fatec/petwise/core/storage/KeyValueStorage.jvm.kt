package edu.fatec.petwise.core.storage

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*

actual object KeyValueStorage {
    private val properties = Properties()
    private val file = File(System.getProperty("user.home"), ".petwise.properties")

    init {
        load()
    }

    private fun load() {
        if (file.exists()) {
            try {
                FileInputStream(file).use { properties.load(it) }
            } catch (e: Exception) {
                
            }
        }
    }

    private fun save() {
        try {
            file.parentFile?.mkdirs()
            FileOutputStream(file).use { properties.store(it, "PetWise Settings") }
        } catch (e: Exception) {
            
        }
    }

    actual fun putString(key: String, value: String) {
        properties.setProperty(key, value)
        save()
    }

    actual fun getString(key: String): String? {
        return properties.getProperty(key)
    }

    actual fun putLong(key: String, value: Long) {
        properties.setProperty(key, value.toString())
        save()
    }

    actual fun getLong(key: String): Long? {
        return properties.getProperty(key)?.toLongOrNull()
    }

    actual fun remove(key: String) {
        properties.remove(key)
        save()
    }

    actual fun clear() {
        properties.clear()
        save()
    }
}