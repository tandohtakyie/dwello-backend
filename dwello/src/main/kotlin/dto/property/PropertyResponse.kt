package dto.property

import kotlinx.serialization.Serializable
import model.property.ListingType

@Serializable
data class PropertyResponse(
    val id: String,
    val name: String,
    val type: String,
    val listingType: ListingType, // *** NEW ***
    val description: String?,
    val price: Double,            // *** UPDATED ***
    val location: String,
    val isAvailable: Boolean,
    val sizeInSquareMeters: Double?,
    val images: List<String>,
    val amenities: List<String>,
    val propertyOwnerId: String,
    val createdAt: String,        // E.g., "2023-10-27T10:15:30"
    val updatedAt: String,
    val leaseTerms: String?,
    val saleTerms: String?,       // *** NEW ***
    val rating: Float?,
    var isFavorited: Boolean = false // Dynamically set based on current user
)
