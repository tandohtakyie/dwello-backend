package dto.user.auth

import kotlinx.serialization.Serializable
import model.user.UserRole

@Serializable
data class RegisterUserRequest(
    val email: String,
    val password: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val role: UserRole // Client specifies the role they are signing up for
)
