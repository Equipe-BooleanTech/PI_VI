package edu.fatec.petwise.features.auth.shared

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import kotlinx.serialization.Serializable

@Serializable
data class InputMaskPattern(
    val pattern: String,
    val placeholder: String = "_"
)

object InputMasks {
    val CPF = InputMaskPattern("###.###.###-##")
    val CNPJ = InputMaskPattern("##.###.###/####-##")
    val PHONE = InputMaskPattern("(##) #####-####")
    val CEP = InputMaskPattern("#####-###")
    val DATE = InputMaskPattern("##/##/####")
    val CREDIT_CARD = InputMaskPattern("#### #### #### ####")
    val CRMV = InputMaskPattern("######")
    
    val MASK_MAP = mapOf(
        "cpf" to CPF,
        "cnpj" to CNPJ, 
        "phone" to PHONE,
        "cep" to CEP,
        "date" to DATE,
        "creditCard" to CREDIT_CARD,
        "crmv" to CRMV
    )

    fun applyMask(text: String, pattern: String, placeholder: String = "_"): String {
        if (text.isEmpty()) return ""

        val digitsOnly = text.filter { it.isDigit() || it.isLetter() }
        var result = ""
        var digitIndex = 0

        for (patternChar in pattern) {
            if (digitIndex >= digitsOnly.length) {
                break
            }
            
            result += if (patternChar == '#') {
                digitsOnly[digitIndex++]
            } else {
                patternChar
            }
        }

        return result
    }

    fun removeMask(maskedText: String): String {
        return maskedText.filter { it.isDigit() || it.isLetter() }
    }

    fun processTextWithMask(
        previousValue: TextFieldValue,
        newText: String,
        mask: InputMaskPattern
    ): TextFieldValue {
        val rawNewText = removeMask(newText)
        val maskedText = applyMask(rawNewText, mask.pattern, mask.placeholder)
        
        val oldMaskedText = previousValue.text
        val oldCursorPosition = previousValue.selection.start
        
        val isShorter = maskedText.length < oldMaskedText.length
        
        val newCursorPosition = if (isShorter) {
            oldCursorPosition
        } else {
            val charsAdded = maskedText.length - oldMaskedText.length
            val newPos = oldCursorPosition + charsAdded
            newPos.coerceAtMost(maskedText.length)
        }
        
        return TextFieldValue(
            text = maskedText,
            selection = TextRange(newCursorPosition)
        )
    }
}