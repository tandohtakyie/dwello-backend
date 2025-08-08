package routes.user

import auth.UserPrincipal
import dto.user.FavoriteRequest
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import model.user.UserRole
import service.property.PropertyService

fun Route.userRoutes(
    propertyService: PropertyService
) {
    authenticate("auth-jwt") {
        route("/user") {
            // GET /api/v1/user/properties - Get properties listed by the authenticated owner
            get("/properties") {
                val principal = call.principal<UserPrincipal>()!!
                if (principal.role != UserRole.PROPERTY_OWNER) {
                    return@get call.respond(
                        HttpStatusCode.Forbidden,
                        "Only property owners can view their listed properties here."
                    )
                }
                propertyService.getPropertiesByOwner(
                    principal.userId,
                    principal.userId
                ) // Pass self as current user for favorites
                    .onSuccess { properties -> call.respond(HttpStatusCode.OK, properties) }
                    .onFailure { error ->
                        call.respond(
                            HttpStatusCode.InternalServerError,
                            mapOf("error" to error.message)
                        )
                    }
            }

            route("/favorites") {
                // POST /api/v1/user/favorites - Add a property to favorites
                post {
                    val principal = call.principal<UserPrincipal>()!!
                    try {
                        val request = call.receive<FavoriteRequest>()
                        propertyService.addPropertyToFavorites(principal.userId, request.propertyId)
                            .onSuccess {
                                call.respond(
                                    HttpStatusCode.Created,
                                    "Property added to favorites."
                                )
                            }
                            .onFailure { error ->
                                val statusCode =
                                    if (error is NoSuchElementException) HttpStatusCode.NotFound else HttpStatusCode.BadRequest
                                call.respond(
                                    statusCode,
                                    mapOf(
                                        "error" to (error.message ?: "Failed to add to favorites.")
                                    )
                                )
                            }
                    } catch (e: Exception) {
                        call.respond(
                            HttpStatusCode.BadRequest,
                            mapOf("error" to (e.message ?: "Invalid request."))
                        )
                    }
                }

                // DELETE /api/v1/user/favorites/{propertyId} - Remove a property from favorites
                delete("/{propertyId}") {
                    val principal = call.principal<UserPrincipal>()!!
                    val propertyId = call.parameters["propertyId"] ?: return@delete call.respond(
                        HttpStatusCode.BadRequest,
                        "Property ID missing."
                    )

                    propertyService.removePropertyFromFavorites(principal.userId, propertyId)
                        .onSuccess {
                            call.respond(
                                HttpStatusCode.OK,
                                "Property removed from favorites."
                            )
                        }
                        .onFailure { error ->
                            val statusCode =
                                if (error is NoSuchElementException) HttpStatusCode.NotFound else HttpStatusCode.BadRequest
                            call.respond(
                                statusCode,
                                mapOf(
                                    "error" to (error.message ?: "Failed to remove from favorites.")
                                )
                            )
                        }
                }

                // GET /api/v1/user/favorites - Get all properties favorited by the user
                get {
                    val principal = call.principal<UserPrincipal>()!!
                    propertyService.getUserFavoriteProperties(principal.userId)
                        .onSuccess { favoriteProperties ->
                            call.respond(
                                HttpStatusCode.OK,
                                favoriteProperties
                            )
                        }
                        .onFailure { error ->
                            call.respond(
                                HttpStatusCode.InternalServerError,
                                mapOf("error" to error.message)
                            )
                        }
                }
            }
        }
    }
}