package dto.user.auth

import kotlinx.serialization.Serializable
import model.user.UserRole

@Serializable
data class TokenResponse( // Sent back to the client after successful login
    val token: String,
    val userId: String,
    val email: String,
    val role: UserRole
)
