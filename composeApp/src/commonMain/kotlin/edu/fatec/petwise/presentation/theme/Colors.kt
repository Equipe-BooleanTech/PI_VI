package edu.fatec.petwise.presentation.theme

import androidx.compose.ui.graphics.Color

val AzulProfundo = Color(0xFF2C3E50)
val VerdeMenta = Color(0xFF27AE60)
val Branco = Color(0xFFFFFFFF)
val AmareloDourado = Color(0xFFF1C40F)


/**
 * Converte uma string hexadecimal para um objeto Color.
 * Suporta formatos com e sem canal alfa (ARGB ou RGB).
 * Se a string não for válida, retorna Color.Black.
 */
fun Color.Companion.fromHex(hex: String): Color {
    val cleanHex = hex.removePrefix("#")
    return when (cleanHex.length) {
        6 -> {
            val r = cleanHex.substring(0, 2).toInt(16)
            val g = cleanHex.substring(2, 4).toInt(16)
            val b = cleanHex.substring(4, 6).toInt(16)
            Color(r, g, b)
        }
        8 -> {
            val a = cleanHex.substring(0, 2).toInt(16)
            val r = cleanHex.substring(2, 4).toInt(16)
            val g = cleanHex.substring(4, 6).toInt(16)
            val b = cleanHex.substring(6, 8).toInt(16)
            Color(r, g, b, a)
        }
        else -> Color.Black
    }
}

/**
 * Converte uma string hexadecimal para um objeto Color.
 * Suporta formatos com e sem canal alfa (ARGB ou RGB).
 * Se a string não for válida, retorna Color.Black.
 */
fun fromHex(hex: String): Color = Color.fromHex(hex)