package edu.fatec.petwise.presentation.shared.form

internal actual fun currentTimeMs(): Long =
    kotlin.js.Date().getTime().toLong()
