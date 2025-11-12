package edu.fatec.petwise.features.food.domain.models

data class Food(
    val id: String,
    val name: String,
    val brand: String,
    val category: String,
    val description: String?,
    val price: Double,
    val stock: Int,
    val unit: String,
    val expiryDate: String?,
    val imageUrl: String?,
    val active: Boolean,
    val createdAt: String,
    val updatedAt: String
)
