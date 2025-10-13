package edu.fatec.petwise.presentation.shared.form
import kotlinx.datetime.Clock


internal actual fun currentTimeMs(): Long = Clock.System.now().toEpochMilliseconds()
