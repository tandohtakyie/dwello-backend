package dto.request.property

import kotlinx.serialization.Serializable

/**
 * Paginated response for list endpoints
 */
@Serializable
data class PaginatedResponse<T>(
    val items: List<T>,
    val total: Long,
    val page: Int,
    val pageSize: Int,
    val totalPages: Int
)