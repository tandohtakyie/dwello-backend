package auth

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.jwt
import model.user.UserRole
import utils.AppEnvironment
import utils.Constants.Authentication.EMAIL
import utils.Constants.Authentication.ROLE
import utils.Constants.Authentication.USER_ID

data class UserPrincipal(
    val userId: String,
    val email: String,
    val role: UserRole
)

fun Application.configureSecurity() {

    JwtConfig.initializeFromEnv()

    install(Authentication) {
        jwt("auth-jwt") { // This is the name of the authentication provider
            realm = AppEnvironment.JWT_REALM
            verifier(JwtConfig.instance.verifier) // Use the verifier from your JwtConfig

            validate { credential ->
                // Extract claims from the JWT payload
                val userId = credential.payload.getClaim(USER_ID).asString()
                val email = credential.payload.getClaim(EMAIL).asString()
                val roleString = credential.payload.getClaim(ROLE).asString()

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