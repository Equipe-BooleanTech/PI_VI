package edu.fatec.petwise.presentation.shared.form.masking

import edu.fatec.petwise.features.auth.shared.InputMasks

/**
 * Bridge para integração de máscaras de entrada em formulários
 * TODO: Implementar a aplicação real da máscara no campo de texto
 */
object InputMaskBridge {

    fun shouldUseMask(fieldId: String, formatting: Any?): Boolean {

        val maskableFields = setOf("cpf", "cnpj", "phone", "cep", "crmv")


        return maskableFields.any { fieldId.contains(it, ignoreCase = true) } ||
                (formatting != null)
    }

    fun getMaskForFieldId(fieldId: String): String? {
        return when {
            fieldId.contains("cpf", ignoreCase = true) -> InputMasks.MASK_MAP["cpf"]
            fieldId.contains("cnpj", ignoreCase = true) -> InputMasks.MASK_MAP["cnpj"]
            fieldId.contains("phone", ignoreCase = true) -> InputMasks.MASK_MAP["phone"]
            fieldId.contains("cep", ignoreCase = true) -> InputMasks.MASK_MAP["cep"]
            fieldId.contains("crmv", ignoreCase = true) -> InputMasks.MASK_MAP["crmv"]
            else -> null
        }
    }

    fun getMaskFromFormatting(formatting: Map<String, Any?>?): String? {
        return formatting?.get("mask") as? String
    }
}