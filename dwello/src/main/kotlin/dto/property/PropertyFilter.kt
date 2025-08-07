package dto.property

import kotlinx.serialization.Serializable
import model.property.ListingType

/**
 * Filter criteria for property searches
 */
@Serializable
data class PropertyFilter(
    val type: String? = null,
    val location: String? = null,
    val minPrice: Int? = null,
    val maxPrice: Int? = null,
    val minSize: Double? = null,
    val listingType: ListingType? = null,
    val maxSize: Double? = null,
    val isAvailable: Boolean? = null,
    val amenities: List<String>? = null,
    val propertyOwnerId: String? = null,
)