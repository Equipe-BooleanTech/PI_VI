package edu.fatec.petwise.features.auth.shared

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

/**
 * Máscaras de entrada comuns para formulários em geral
 */
object InputMasks {

    val MASK_MAP: Map<String, String> = mapOf(
        "cpf" to "###.###.###-##",
        "cnpj" to "##.###.###/####-##",
        "phone" to "(##) #####-####",
        "cep" to "#####-###",
        "crmv" to "####-##"
    )

    /**
     * Aplica máscara ao texto baseado no padrão fornecido
     * @param currentValue Valor atual do campo
     * @param newText Novo texto inserido
     * @param mask Padrão da máscara (ex: "###.###.###-##")
     * @return TextFieldValue com máscara aplicada
     */
    fun processTextWithMask(
        currentValue: TextFieldValue,
        newText: String,
        mask: String?
    ): TextFieldValue {
        if (mask.isNullOrEmpty()) {
            return TextFieldValue(
                text = newText,
                selection = TextRange(newText.length)
            )
        }

        val cleanText = newText.replace(Regex("[^0-9a-zA-Z]"), "")
        
        val maskedResult = applyMask(cleanText, mask)
        
        val cursorPosition = calculateCursorPosition(
            oldText = currentValue.text,
            newText = maskedResult.text,
            oldCursor = currentValue.selection.end,
            inputLength = cleanText.length
        )
        
        return TextFieldValue(
            text = maskedResult.text,
            selection = TextRange(cursorPosition)
        )
    }

    /**
     * Apply mask pattern to clean text
     */
    private fun applyMask(cleanText: String, mask: String): ProcessedMaskValue {
        val result = StringBuilder()
        var textIndex = 0
        var isComplete = true
        
        for (maskChar in mask) {
            when (maskChar) {
                '#' -> {
                    if (textIndex < cleanText.length) {
                        result.append(cleanText[textIndex])
                        textIndex++
                    } else {
                        isComplete = false
                        break
                    }
                }
                else -> {
                    if (textIndex < cleanText.length) {
                        result.append(maskChar)
                    }
                }
            }
        }
        
        return ProcessedMaskValue(
            text = result.toString(),
            cursorPosition = result.length,
            isComplete = isComplete && textIndex == cleanText.length
        )
    }

    private fun calculateCursorPosition(
        oldText: String,
        newText: String,
        oldCursor: Int,
        inputLength: Int
    ): Int {
        if (newText.isEmpty()) return 0
        if (oldCursor >= newText.length) return newText.length
        
        if (newText.length > oldText.length) {
            return newText.length
        }
        
        return minOf(oldCursor, newText.length)
    }

    /**
     * Remove máscara do texto, deixando apenas caracteres alfanuméricos
     */
    fun removeMask(maskedValue: String): String {
        return maskedValue.replace(Regex("[^0-9a-zA-Z]"), "")
    }

    /**
     * Verifica se o texto está completo para a máscara
     */
    fun isTextComplete(text: String, mask: String): Boolean {
        val cleanText = removeMask(text)
        val maskDigits = mask.count { it == '#' }
        return cleanText.length == maskDigits
    }

    /**
     * Obtém máscara baseada no tipo de campo
     */
    fun getMaskForFieldType(fieldId: String, fieldType: String): String? {
        return when {
            fieldId.contains("cpf", ignoreCase = true) || fieldType == "cpf" -> MASK_MAP["cpf"]
            fieldId.contains("cnpj", ignoreCase = true) || fieldType == "cnpj" -> MASK_MAP["cnpj"]
            fieldId.contains("phone", ignoreCase = true) || fieldType == "phone" -> MASK_MAP["phone"]
            fieldId.contains("cep", ignoreCase = true) || fieldType == "cep" -> MASK_MAP["cep"]
            fieldId.contains("crmv", ignoreCase = true) || fieldType == "crmv" -> MASK_MAP["crmv"]
            else -> null
        }
    }
}

data class ProcessedMaskValue(
    val text: String,
    val cursorPosition: Int,
    val isComplete: Boolean
)