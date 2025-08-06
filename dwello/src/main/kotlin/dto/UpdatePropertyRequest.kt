package dto

import kotlinx.serialization.Serializable

/**
 * Request DTO for updating an existing property
 */
@Serializable
data class UpdatePropertyRequest(
    val name: String? = null,
    val type: String? = null,
    val description: String? = null,
    val pricePerMonth: Int? = null,
    val location: String? = null,
    val isAvailable: Boolean? = null,
    val sizeInSquareMeters: Double? = null,
    val images: List<String>? = null,
    val amenities: List<String>? = null,
    val leaseTerms: String? = null,
    val rating: Float? = null,
)