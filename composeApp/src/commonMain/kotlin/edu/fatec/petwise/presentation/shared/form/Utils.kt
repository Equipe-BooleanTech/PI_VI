package edu.fatec.petwise.presentation.shared.form

/**
 * Utility function to get current time in milliseconds
 * Platform-agnostic replacement for System.currentTimeMillis()
 */
internal expect fun currentTimeMs(): Long

/**
 * Generate a unique ID based on current timestamp
 */
internal fun generateId(prefix: String = ""): String {
    return if (prefix.isNotEmpty()) {
        "${prefix}_${currentTimeMs()}"
    } else {
        currentTimeMs().toString()
    }
}