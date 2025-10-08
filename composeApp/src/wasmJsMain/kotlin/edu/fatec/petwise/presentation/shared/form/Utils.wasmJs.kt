package edu.fatec.petwise.presentation.shared.form

import kotlin.js.Date

/**
 * WASM-JS implementation of currentTimeMs
 * Uses kotlin.js.Date since kotlin.system is not available in WASM-JS
 */
internal actual fun currentTimeMs(): Long = Date.now().toLong()