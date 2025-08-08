package routes.property

import auth.UserPrincipal
import dto.property.CreatePropertyRequest
import dto.property.UpdatePropertyRequest
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import model.user.UserRole
import service.property.PropertyService
import utils.Constants

fun Route.propertyRoutes(
    propertyService: PropertyService
) {
    route("/properties") {
        // GET /api/v1/properties - Get all properties
        get {
            val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
            val pageSize = call.request.queryParameters["pageSize"]?.toIntOrNull()
                ?: Constants.Defaults.PAGE_SIZE

            val principal = call.principal<UserPrincipal>()
            val currentUserId = principal?.userId

            propertyService.getAllProperties(page, pageSize, currentUserId)
                .onSuccess { properties ->
                    call.respond(HttpStatusCode.OK, properties)
                }
                .onFailure { error ->
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        mapOf("error" to error.message)
                    )
                }
        }

        // GET /api/v1/properties/{id} - Get a specific property by ID
        get("{id}") {
            val propertyId = call.parameters["id"] ?: return@get call.respond(
                HttpStatusCode.BadRequest,
                "Property ID missing"
            )
            val principal = call.principal<UserPrincipal>()
            val currentId = principal?.userId

            propertyService.getPropertyById(propertyId, currentId)
                .onSuccess { property -> call.respond(HttpStatusCode.OK, property) }
                .onFailure { error ->
                    if (error is NoSuchElementException) {
                        call.respond(
                            HttpStatusCode.NotFound,
                            mapOf("error" to error.message)
                        )
                    } else {
                        call.respond(
                            HttpStatusCode.InternalServerError,
                            mapOf("error" to error.message)
                        )
                    }
                }
        }

        authenticate("auth-jwt") {
            // POST /api/v1/properties - Create a new property
            post {
                val principal = call.principal<UserPrincipal>()!!
                if (principal.role != UserRole.PROPERTY_OWNER) {
                    return@post call.respond(
                        HttpStatusCode.Forbidden,
                        "Only property owners can create property listings."
                    )
                }
                try {
                    val request = call.receive<CreatePropertyRequest>()
                    propertyService.createProperty(principal.userId, principal.role, request)
                        .onSuccess { createdProperty ->
                            call.respond(
                                HttpStatusCode.Created,
                                createdProperty
                            )
                        }
                        .onFailure { error ->
                            val statusCode =
                                if (error is SecurityException) HttpStatusCode.Forbidden else HttpStatusCode.BadRequest
                            call.respond(
                                statusCode,
                                mapOf("error" to (error.message ?: "Failed to create property."))
                            )
                        }
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("error" to (e.message ?: "Invalid request body"))
                    )
                }
            }

            // PUT /api/v1/properties/{id} - Update an existing property
            put("/{id}") {
                val principal = call.principal<UserPrincipal>()!!
                val propertyId = call.parameters["id"] ?: return@put call.respond(
                    HttpStatusCode.BadRequest,
                    "Property ID missing"
                )

                // Owners can only update their own properties. Admins could update any (future).
                // This check is also done in the service layer for robustness.
                // if (principal.role != UserRole.PROPERTY_OWNER && principal.role != UserRole.ADMIN) {
                //    return@put call.respond(HttpStatusCode.Forbidden, "Not authorized to update properties.")
                // }
                try {
                    val request = call.receive<UpdatePropertyRequest>()
                    propertyService.updateProperty(
                        propertyId,
                        principal.userId,
                        request
                    ) // Service checks ownership
                        .onSuccess { updatedProperty ->
                            call.respond(
                                HttpStatusCode.OK,
                                updatedProperty
                            )
                        }
                        .onFailure { error ->
                            val statusCode = when (error) {
                                is NoSuchElementException -> HttpStatusCode.NotFound
                                is SecurityException -> HttpStatusCode.Forbidden
                                else -> HttpStatusCode.BadRequest
                            }
                            call.respond(
                                statusCode,
                                mapOf("error" to (error.message ?: "Failed to update property"))
                            )
                        }
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("error" to (e.message ?: "Invalid request body."))
                    )
                }
            }

            // DELETE /api/v1/properties/{id} - Delete a property
            delete("/{id}") {
                val principal = call.principal<UserPrincipal>()!!
                val propertyId = call.parameters["id"] ?: return@delete call.respond(
                    HttpStatusCode.BadRequest,
                    "Property ID missing"
                )

                propertyService.deleteProperty(
                    propertyId,
                    principal.userId
                )
                    .onSuccess { call.respond(HttpStatusCode.NoContent) }
                    .onFailure { error ->
                        val statusCode = when (error) {
                            is NoSuchElementException -> HttpStatusCode.NotFound
                            is SecurityException -> HttpStatusCode.Forbidden
                            else -> HttpStatusCode.InternalServerError
                        }
                        call.respond(
                            statusCode,
                            mapOf("error" to (error.message ?: "Failed to delete property"))
                        )
                    }
            }
        }
    }
}