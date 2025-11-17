package edu.fatec.petwise.core.network.dto

import kotlinx.serialization.Serializable
import edu.fatec.petwise.features.suprimentos.domain.models.Suprimento
import edu.fatec.petwise.features.suprimentos.domain.models.SuprimentCategory

@Serializable
data class SuprimentoDto(
    val id: String = "",
    val petId: String,
    val description: String,
    val category: String,
    val price: Float,
    val orderDate: String,
    val shopName: String,
    val createdAt: String = "",
    val updatedAt: String = ""
)

@Serializable
data class SuprimentoListResponse(
    val suprimentos: List<SuprimentoDto>? = null,
    val total: Int
)

fun SuprimentoDto.toDomain(): Suprimento {
    return Suprimento(
        id = id,
        petId = petId,
        description = description,
        category = SuprimentCategory.valueOf(category),
        price = price,
        orderDate = orderDate,
        shopName = shopName,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun Suprimento.toDto(): SuprimentoDto {
    return SuprimentoDto(
        id = id,
        petId = petId,
        description = description,
        category = category.name,
        price = price,
        orderDate = orderDate,
        shopName = shopName,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}