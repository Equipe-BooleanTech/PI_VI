package edu.fatec.petwise.core.network.dto

import kotlinx.serialization.Serializable
import edu.fatec.petwise.features.hygiene.domain.models.HygieneProduct

@Serializable
data class HygieneDto(
    val id: String?,
    val name: String,
    val brand: String,
    val category: String,
    val description: String? = null,
    val price: Double,
    val stock: Int,
    val unit: String,
    val expiryDate: String? = null,
    val imageUrl: String? = null,
    val active: Boolean = true,
    val createdAt: String,
    val updatedAt: String
)

@Serializable
data class HygieneListResponse(
    val hygieneProducts: List<HygieneDto>? = null,
    val total: Int,
    val page: Int,
    val pageSize: Int
)

@Serializable
data class CreateHygieneRequest(
    val name: String,
    val brand: String,
    val category: String,
    val description: String? = null,
    val price: Double,
    val stock: Int,
    val unit: String,
    val expiryDate: String? = null,
    val imageUrl: String? = null,
    val active: Boolean = true
)

@Serializable
data class UpdateHygieneRequest(
    val name: String? = null,
    val brand: String? = null,
    val category: String? = null,
    val description: String? = null,
    val price: Double? = null,
    val stock: Int? = null,
    val unit: String? = null,
    val expiryDate: String? = null,
    val imageUrl: String? = null,
    val active: Boolean? = null
)

fun HygieneDto.toHygieneProduct(): HygieneProduct {
    return HygieneProduct(
        id = id ?: "",
        name = name,
        brand = brand,
        category = category,
        description = description,
        price = price,
        stock = stock,
        unit = unit,
        expiryDate = expiryDate,
        imageUrl = imageUrl,
        active = active,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
