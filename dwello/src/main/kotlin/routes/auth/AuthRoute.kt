package routes.auth

import dto.user.auth.LoginRequest
import dto.user.auth.RegisterUserRequest
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import service.user.auth.AuthService

fun Route.authRoutes(authService: AuthService) {
    route("/auth") {
        post("/register") {
            try {
                val request = call.receive<RegisterUserRequest>()
                authService.registerUser(request)
                    .onSuccess { userResponse ->
                        call.respond(HttpStatusCode.Created, userResponse)
                    }
                    .onFailure { error ->
                        call.respond(
                            HttpStatusCode.BadRequest,
                            mapOf("error" to (error.message ?: "Registration failed"))
                        )
                    }
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("error" to (e.message ?: "Invalid request format"))
                )
            }
        }
        post("/login") {
            try {
                val request = call.receive<LoginRequest>()
                authService.loginUser(request)
                    .onSuccess { tokenResponse ->
                        call.respond(HttpStatusCode.OK, tokenResponse)
                    }
                    .onFailure { error ->
                        if (error is IllegalArgumentException) {
                            call.respond(
                                HttpStatusCode.Unauthorized,
                                mapOf("error" to (error.message ?: "Login failed"))
                            )
                        } else {
                            call.respond(
                                HttpStatusCode.InternalServerError,
                                mapOf("error" to "An unexpected error occurred.")
                            )
                        }
                    }
            } catch (e: Exception) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    mapOf("error" to (e.message ?: "Invalid request format"))
                )
            }
        }
    }
}