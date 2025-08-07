package auth

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import model.user.UserRole
import utils.Constants
import utils.Constants.Authentication.AUTHENTICATION
import utils.Constants.Authentication.EMAIL
import utils.Constants.Authentication.ROLE
import utils.Constants.Authentication.USER_ID
import utils.Constants.ErrorMessages.JWT_CONFIG_NOT_INITIALIZED
import java.util.Date

class JwtConfig private constructor(
    secret: String
) {
    private val algorithm = Algorithm.HMAC256(secret)
    private val validityInMs: Long = 3_600_000 * 24
    private val issuer = Constants.Authentication.APP_ISSUER

    val verifier: JWTVerifier = JWT
        .require(algorithm)
        .withIssuer(issuer)
        .build()

    fun generateToken(userId: String, email: String, role: UserRole): String = JWT
        .create()
        .withSubject(AUTHENTICATION)
        .withIssuer(issuer)
        .withClaim(USER_ID, userId)
        .withClaim(EMAIL, email)
        .withClaim(ROLE, role.name)
        .withExpiresAt(getExpiration())
        .sign(algorithm)

    private fun getExpiration() = Date(System.currentTimeMillis() + validityInMs)

    companion object {
        @Volatile
        private var INSTANCE: JwtConfig? = null

        fun getInstance(secret: String): JwtConfig =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: JwtConfig(secret).also { INSTANCE = it }
            }

        fun initialize(secret: String) {
            INSTANCE = JwtConfig(secret)
        }

        val instance: JwtConfig
            get() = INSTANCE ?: throw IllegalStateException(JWT_CONFIG_NOT_INITIALIZED)
    }

}