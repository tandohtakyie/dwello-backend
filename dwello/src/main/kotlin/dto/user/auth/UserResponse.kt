package dto.user.auth

import kotlinx.serialization.Serializable
import model.user.User
import model.user.UserRole

@Serializable
data class UserResponse( // For sending user profile information (without sensitive data)
    val id: String,
    val email: String,
    val firstName: String?,
    val lastName: String?,
    val role: UserRole,
    val createdAt: String // Usually a string representation of LocalDateTime
)

fun User.toUserResponse() = UserResponse(
    id = id,
    email = email,
    firstName = firstName,
    lastName = lastName,
    role = role,
    createdAt = createdAt.toString()
)


