package edu.fatec.petwise.features.toys.domain.models

data class Toy(
    val id: String,
    val name: String,
    val brand: String,
    val category: String,
    val description: String?,
    val price: Double,
    val stock: Int,
    val unit: String,
    val material: String?,
    val ageRecommendation: String?,
    val imageUrl: String?,
    val active: Boolean,
    val createdAt: String,
    val updatedAt: String
)
