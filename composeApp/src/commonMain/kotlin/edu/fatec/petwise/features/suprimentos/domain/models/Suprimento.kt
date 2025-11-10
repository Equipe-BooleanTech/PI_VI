package edu.fatec.petwise.features.suprimentos.domain.models

import kotlinx.serialization.Serializable

@Serializable
data class Suprimento(
    val id: String = "",
    val petId: String,
    val description: String,
    val category: SuprimentCategory,
    val price: Float,
    val orderDate: String,
    val shopName: String,
    val createdAt: String = "",
    val updatedAt: String = ""
)

@Serializable
enum class SuprimentCategory(val displayName: String) {
    ACCESSORY("Acessórios"),
    TOY("Brinquedo"),
    BED("Cama"),
    HYGIENE("Higiene"),
    FOOD("Alimentação"),
    CLOTHES("Roupas"),
    MEDICATION("Medicamentos"),
    OTHER("Outro");

    companion object {
        fun fromDisplayName(displayName: String): SuprimentCategory {
            return values().find { it.displayName == displayName } ?: OTHER
        }

        fun getAllDisplayNames(): List<String> {
            return values().map { it.displayName }
        }
    }
}

@Serializable
data class SuprimentoFilterOptions(
    val petId: String? = null,
    val category: SuprimentCategory? = null,
    val searchQuery: String? = null,
    val minPrice: Float? = null,
    val maxPrice: Float? = null,
    val shopName: String? = null
)

@Serializable
data class SuprimentoSearchCriteria(
    val query: String,
    val filterBy: List<String> = listOf("description", "shopName", "category")
)