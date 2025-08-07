package dto.request.response

import kotlinx.serialization.Serializable

/**
 * Generic API response wrapper
 */
@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val message: String? = null,
    val data: T? = null,
    val error: String? = null,
)