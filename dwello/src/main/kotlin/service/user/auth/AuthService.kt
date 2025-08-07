package service.user.auth

import auth.JwtConfig
import auth.PasswordHasher
import dto.user.auth.LoginRequest
import dto.user.auth.RegisterUserRequest
import dto.user.auth.TokenResponse
import dto.user.auth.UserResponse
import dto.user.auth.toUserResponse
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import model.user.User
import repository.user.UserRepository

class AuthService(
    private val userRepository: UserRepository,
    private val passwordHasher: PasswordHasher,
    private val jwtConfig: JwtConfig,
) {
    suspend fun registerUser(request: RegisterUserRequest): Result<UserResponse> {
        if (userRepository.findUserByEmail(request.email.lowercase()) != null) {
            return Result.failure(IllegalArgumentException("User with this email already exists"))
        }

        val now = Clock.System.now().toLocalDateTime(TimeZone.UTC)
        val user = User(
            email = request.email.lowercase(),
            passwordHash = passwordHasher.hashPassword(request.password),
            firstName = request.firstName,
            lastName = request.lastName,
            role = request.role,
            createdAt = now,
            updatedAt = now
        )

        val createdUser = userRepository.createUser(user)
        return if (createdUser != null) {
            Result.success(createdUser.toUserResponse())
        } else {
            Result.failure(RuntimeException("Failed to create user"))
        }
    }

    suspend fun loginUser(request: LoginRequest): Result<TokenResponse> {
        val user = userRepository.findUserByEmail(request.email.lowercase())
            ?: return Result.failure(IllegalArgumentException("Invalid email or password"))

        if (!user.isActive) {
            return Result.failure(IllegalAccessException("User account is not active"))
        }

        if (!passwordHasher.checkPassword(request.password, user.passwordHash)) {
            return Result.failure(IllegalArgumentException("Invalid email or password"))
        }

        val token = jwtConfig.generateToken(user.id, user.email, user.role)
        return Result.success(TokenResponse(token, user.id, user.email, user.role))
    }
}