package auth

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import model.user.UserRole
import utils.AppEnvironment
import utils.Constants.Authentication.EMAIL
import utils.Constants.Authentication.ROLE
import utils.Constants.Authentication.USER_ID
import java.util.Date

class JwtConfig private constructor(
    secret: String,
    val issuer: String,
    private val validityInMs: Long,
) {

    private val algorithm = Algorithm.HMAC256(secret)

    val verifier: JWTVerifier = JWT
        .require(algorithm)
        .withIssuer(issuer)
        .build()

    fun generateToken(userId: String, email: String, role: UserRole): String = JWT.create()
        .withSubject("Authentication")
        .withIssuer(issuer)
        .withClaim(USER_ID, userId)
        .withClaim(EMAIL, email)
        .withClaim(ROLE, role.name)
        .withExpiresAt(Date(System.currentTimeMillis() + validityInMs))
        .sign(algorithm)

    companion object {
        @Volatile
        private var INSTANCE: JwtConfig? = null

        // Public method to explicitly initialize or re-initialize from environment
        fun initializeFromEnv() {
            synchronized(this) {
                INSTANCE = createInstanceFromEnv()
            }
        }

        // Internal method to create an instance, called by getInstance or initializeFromEnv
        private fun createInstanceFromEnv(): JwtConfig {
            val secret = AppEnvironment.JWT_SECRET
            val issuer = AppEnvironment.JWT_ISSUER
            val validityHours = AppEnvironment.JWT_EXPIRATION_HOURS
            val validityInMs = validityHours * 3_600_000L // 1 hour = 3,600,000 ms

            return JwtConfig(secret, issuer, validityInMs)
        }

        // Singleton accessor
        val instance: JwtConfig
            get() = INSTANCE ?: synchronized(this) {
                INSTANCE ?: createInstanceFromEnv().also { INSTANCE = it }
            }
    }
}