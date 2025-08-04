package domain.property.model

import java.time.LocalDateTime

data class Property(
    val name: String,
    val type: String,
    val description: String,
    val pricePerMonth: Int,
    val location: String,
    val isAvailable: Boolean,
    val sizeInSquareMeters: Double,
    val images: List<String>,
    val amenities: List<String>,
    val propertyOwnerId: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val leaseTerms: String,
    val rating: Float,
)
