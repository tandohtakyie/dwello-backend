package model.user

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import serialization.LocalDateTimeSerializer

@Serializable
data class User(
    @BsonId
    val id: String = ObjectId().toHexString(),
    val email: String,                      // Primary identifier for login; should be unique
    val passwordHash: String,               // Stores the hashed password (NEVER plain text)
    val firstName: String? = null,
    val lastName: String? = null,
    val role: UserRole,                     // Defines what the user can do
    val favoritePropertyIds: List<String> = emptyList(), // Stores IDs of properties the user has in their favorite
    @Serializable(with = LocalDateTimeSerializer::class)
    val createdAt: LocalDateTime,
    @Serializable(with = LocalDateTimeSerializer::class)
    val updatedAt: LocalDateTime,
    val isActive: Boolean = true,           // For deactivating accounts
    val emailVerified: Boolean = false,      // For potential email verification process
    val phoneNumber: String? = null,
    val profilePictureUrl: String? = null,
)
