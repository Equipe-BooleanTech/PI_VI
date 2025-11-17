package edu.fatec.petwise.core.storage

expect object KeyValueStorage {
    fun putString(key: String, value: String)
    fun getString(key: String): String?
    fun putLong(key: String, value: Long)
    fun getLong(key: String): Long?
    fun remove(key: String)
    fun clear()
}