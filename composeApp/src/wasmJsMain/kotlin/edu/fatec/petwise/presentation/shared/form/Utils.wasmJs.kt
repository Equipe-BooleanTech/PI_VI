package edu.fatec.petwise.presentation.shared.form

/**
 * WASM-JS implementation of currentTimeMs
 * Uses Kotlin's native time utilities
 */
internal actual fun currentTimeMs(): Long = kotlin.system.getTimeMillis()