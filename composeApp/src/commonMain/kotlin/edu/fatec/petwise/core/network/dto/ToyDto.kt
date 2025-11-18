package edu.fatec.petwise.core.network.dto

import kotlinx.serialization.Serializable
import edu.fatec.petwise.features.toys.domain.models.Toy

@Serializable
data class ToyDto(
    val id: String,
    val name: String,
    val brand: String,
    val category: String,
    val description: String? = null,
    val price: Double,
    val stock: Int,
    val unit: String,
    val material: String? = null,
    val ageRecommendation: String? = null,
    val imageUrl: String? = null,
    val active: Boolean = true,
    val createdAt: String,
    val updatedAt: String
)

@Serializable
data class ToyListResponse(
    val toys: List<ToyDto>? = null,
    val total: Int,
    val page: Int,
    val pageSize: Int
)

@Serializable
data class CreateToyRequest(
    val name: String,
    val brand: String,
    val category: String,
    val description: String? = null,
    val price: Double,
    val stock: Int,
    val unit: String,
    val material: String? = null,
    val ageRecommendation: String? = null,
    val imageUrl: String? = null,
    val active: Boolean = true
)

@Serializable
data class UpdateToyRequest(
    val name: String? = null,
    val brand: String? = null,
    val category: String? = null,
    val description: String? = null,
    val price: Double? = null,
    val stock: Int? = null,
    val unit: String? = null,
    val material: String? = null,
    val ageRecommendation: String? = null,
    val imageUrl: String? = null,
    val active: Boolean? = null
)

fun ToyDto.toToy(): Toy {
    return Toy(
        id = id,
        name = name,
        brand = brand,
        category = category,
        description = description,
        price = price,
        stock = stock,
        unit = unit,
        material = material,
        ageRecommendation = ageRecommendation,
        imageUrl = imageUrl,
        active = active,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
