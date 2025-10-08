package edu.fatec.petwise.features.auth.shared

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

    fun processTextWithMask(
        currentValue: androidx.compose.ui.text.input.TextFieldValue,
        newText: String,
        mask: String?
    ): androidx.compose.ui.text.input.TextFieldValue {
        return androidx.compose.ui.text.input.TextFieldValue(
            text = newText,
            selection = androidx.compose.ui.text.TextRange(newText.length)
        )
    }

    fun removeMask(maskedValue: String): String {
        return maskedValue.replace(Regex("[.()-/\\s-]"), "")
    }
}

data class ProcessedMaskValue(
    val text: String,
    val cursorPosition: Int,
    val isComplete: Boolean
)