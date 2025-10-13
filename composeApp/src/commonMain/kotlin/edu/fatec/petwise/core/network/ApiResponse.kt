package edu.fatec.petwise.core.network

import kotlinx.serialization.Serializable

/**
 * Standard API response wrapper for consistent response handling
 * Includes metadata for pagination, timestamps, and request tracking
 */
@Serializable
data class ApiResponse<T>(
    val data: T,
    val metadata: ApiMetadata? = null,
    val pagination: PaginationMetadata? = null
)

@Serializable
data class ApiMetadata(
    val timestamp: Long,
    val requestId: String? = null,
    val version: String? = null
)

@Serializable
data class PaginationMetadata(
    val page: Int,
    val pageSize: Int,
    val total: Int,
    val totalPages: Int,
    val hasNext: Boolean,
    val hasPrevious: Boolean
)

@Serializable
data class ApiErrorResponse(
    val message: String,
    val code: String? = null,
    val errors: Map<String, List<String>>? = null,
    val timestamp: Long? = null,
    val path: String? = null,
    val requestId: String? = null,
    val details: Map<String, String>? = null
)
