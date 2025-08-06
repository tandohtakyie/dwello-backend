package dto

import kotlinx.serialization.Serializable

/**
 * Request DTO for creating a new property
 */
@Serializable
data class CreatePropertyRequest(
    val name: String,
    val type: String,
    val description: String? = null,
    val pricePerMonth: Int,
    val location: String,
    val sizeInSquareMeters: Double? = null,
    val images: List<String> = emptyList(),
    val amenities: List<String> = emptyList(),
    val propertyOwnerId: String,
    val leaseTerms: String? = null,
)