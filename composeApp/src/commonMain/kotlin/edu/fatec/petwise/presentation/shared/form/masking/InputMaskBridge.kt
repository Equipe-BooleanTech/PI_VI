package edu.fatec.petwise.presentation.shared.form.masking

import edu.fatec.petwise.features.auth.shared.InputMasks

/**
 * Bridge between form fields and input masking system
 * TODO: Implement proper input masking integration
 */
object InputMaskBridge {
    
    /**
     * Determine if a field should use masking based on field ID and formatting
     */
    fun shouldUseMask(fieldId: String, formatting: Any?): Boolean {
        // Check if the field ID corresponds to a known masked field type
        val maskableFields = setOf("cpf", "cnpj", "phone", "cep", "crmv")
        
        // Check if field ID contains any maskable field type
        return maskableFields.any { fieldId.contains(it, ignoreCase = true) } ||
                (formatting != null) // Simple check for now
    }
    
    /**
     * Get appropriate mask for a field ID
     */
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
    
    /**
     * Get mask from formatting configuration
     */
    fun getMaskFromFormatting(formatting: Map<String, Any?>?): String? {
        return formatting?.get("mask") as? String
    }
}