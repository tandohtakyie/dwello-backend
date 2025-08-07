package model.property

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import serialization.LocalDateTimeSerializer

/**
 * Property entity representing a real estate property
 */
@Serializable
data class Property(
    @BsonId
    val id: String = ObjectId().toHexString(),
    val name: String,
    val type: String,
    val listingType: ListingType,
    val description: String? = null,
    val price: Double,
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
    val saleTerms: String? = null,
    val rating: Float? = null,
)
