package dto.property

import kotlinx.serialization.Serializable
import model.property.ListingType

/**
 * Request DTO for creating a new property
 */
@Serializable
data class CreatePropertyRequest(
    val name: String,
    val type: String,
    val listingType: ListingType,
    val description: String? = null,
    val price: Double,
    val location: String,
    val sizeInSquareMeters: Double? = null,
    val images: List<String> = emptyList(),
    val amenities: List<String> = emptyList(),
    val propertyOwnerId: String,
    val leaseTerms: String? = null,
    val saleTerms: String? = null,
)