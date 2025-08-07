package auth

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.jwt
import model.user.UserRole

data class UserPrincipal(
    val userId: String,
    val email: String,
    val role: UserRole
)

fun Application.configureSecurity() {

    // Load JWT secret from environment configuration for better security
    // In application.conf: jwt.secret = "your-super-secret-key"
    // IMPORTANT: Use a strong, randomly generated secret in production, loaded from env variables.

    val jwtSecret = environment.config.propertyOrNull("ktor.jwt.secret")?.getString()
        ?: throw RuntimeException("JWT secret is not configured in application.conf or environment")
    val jwtIssuer = environment.config.property("ktor.jwt.issuer").getString()
    val jwtRealm = environment.config.property("ktor.jwt.realm").getString()

    JwtConfig.initialize(jwtSecret)

    install(Authentication) {
        jwt("auth-jwt") { // This is the name of the authentication provider
            realm = jwtRealm
            verifier(JwtConfig.instance.verifier) // Use the verifier from your JwtConfig

            validate { credential ->
                // Extract claims from the JWT payload
                val userId = credential.payload.getClaim("userId").asString()
                val email = credential.payload.getClaim("email").asString()
                val roleString = credential.payload.getClaim("role").asString()

                if (userId != null && email != null && roleString != null) {
                    try {
                        // Convert role string back to UserRole enum
                        val role = UserRole.valueOf(roleString)
                        UserPrincipal(userId, email, role) // If valid, return the UserPrincipal
                    } catch (e: IllegalArgumentException) {
                        // Invalid role string in token
                        null // Validation fails
                    }
                } else {
                    null // Missing essential claims, validation fails
                }
            }
            challenge { _, _ ->
                // Customize response for authentication failure (e.g., 401 Unauthorized)
                // call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired")
                // For now, Ktor's default challenge will be used.
            }
        }
    }
}