package dto.property

import kotlinx.serialization.Serializable
import model.property.ListingType

/**
 * Request DTO for updating an existing property
 */
@Serializable
data class UpdatePropertyRequest(
    val name: String? = null,
    val type: String? = null,
    val listingType: ListingType? = null,
    val description: String? = null,
    val price: Double? = null,
    val location: String? = null,
    val isAvailable: Boolean? = null,
    val sizeInSquareMeters: Double? = null,
    val images: List<String>? = null,
    val amenities: List<String>? = null,
    val leaseTerms: String? = null,
    val saleTerms: String? = null,
    val rating: Float? = null,
)