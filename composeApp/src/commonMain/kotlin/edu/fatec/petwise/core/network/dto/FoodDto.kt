package edu.fatec.petwise.core.network.dto

import kotlinx.serialization.Serializable
import edu.fatec.petwise.features.food.domain.models.Food

@Serializable
data class FoodDto(
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
data class FoodListResponse(
    val foods: List<FoodDto>? = null,
    val total: Int,
    val page: Int,
    val pageSize: Int
)

@Serializable
data class CreateFoodRequest(
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
data class UpdateFoodRequest(
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

fun FoodDto.toFood(): Food {
    return Food(
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
