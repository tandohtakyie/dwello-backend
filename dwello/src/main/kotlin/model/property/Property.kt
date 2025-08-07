package model.property

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId
import serialization.LocalDateTimeSerializer

/**
 * Property entity representing a real estate property
 */
@Serializable
data class Property(
    val id: String = ObjectId().toHexString(),
    val name: String,
    val type: String,
    val description: String? = null,
    val pricePerMonth: Int,
    val location: String,
    val isAvailable: Boolean = true,
    val sizeInSquareMeters: Double? = null,
    val images: List<String> = emptyList(),
    val amenities: List<String> = emptyList(),
    val propertyOwnerId: String,
    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime,
    @Serializable(with = LocalDateTimeSerializer::class)
    val updatedAt: LocalDateTime,
    val leaseTerms: String? = null,
    val rating: Float? = null,
)