package edu.fatec.petwise.presentation.shared

import kotlin.math.pow

object NumberFormatter {
    fun formatCurrency(value: Double): String {
        return "R$ ${formatDecimal(value, 2)}"
    }

    fun formatDecimal(value: Double, decimalPlaces: Int = 2): String {
        val factor = 10.0.pow(decimalPlaces.toDouble())
        val rounded = kotlin.math.round(value * factor) / factor
        
        
        val integerPart = rounded.toLong()
        val decimalPart = ((rounded - integerPart) * factor).toLong()
        
        return if (decimalPlaces > 0) {
            "$integerPart.${decimalPart.toString().padStart(decimalPlaces, '0')}"
        } else {
            integerPart.toString()
        }
    }
}
