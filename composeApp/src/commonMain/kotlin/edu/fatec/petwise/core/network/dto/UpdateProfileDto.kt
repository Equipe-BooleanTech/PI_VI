package edu.fatec.petwise.core.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class UpdateProfileRequest(
    val fullName: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val cpf: String? = null,
    val crmv: String? = null,
    val specialization: String? = null,
    val cnpj: String? = null,
    val companyName: String? = null
)
