package edu.fatec.petwise.presentation.shared.form

internal expect fun currentTimeMs(): Long

internal fun generateId(prefix: String = ""): String {
    return if (prefix.isNotEmpty()) {
        "${prefix}_${currentTimeMs()}"
    } else {
        currentTimeMs().toString()
    }
}