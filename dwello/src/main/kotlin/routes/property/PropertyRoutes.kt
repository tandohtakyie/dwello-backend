package routes.property

import dto.request.property.CreatePropertyRequest
import dto.request.property.PropertyFilter
import dto.request.property.UpdatePropertyRequest
import dto.request.response.ApiResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import org.koin.ktor.ext.inject
import service.property.PropertyService
import utils.Constants

fun Application.configurePropertyRoutes() {
    val propertyService by inject<PropertyService>()

    routing {
        route(path = Constants.Endpoints.PROPERTIES) {

            // Create property: POST /api/properties
            post {
                try {
                    val request = call.receive<CreatePropertyRequest>()
                    propertyService.createProperty(request)
                        .onSuccess { property ->
                            call.respond(
                                HttpStatusCode.Created, ApiResponse(
                                    success = true,
                                    message = Constants.SuccessMessages.PROPERTY_CREATED,
                                    data = property
                                )
                            )
                        }
                        .onFailure { error ->
                            call.respond(
                                HttpStatusCode.BadRequest, ApiResponse<Nothing>(
                                    success = false,
                                    error = error.message
                                )
                            )
                        }
                } catch (_: Exception) {
                    call.respond(
                        HttpStatusCode.BadRequest, ApiResponse<Nothing>(
                            success = false,
                            error = Constants.ErrorMessages.INVALID_REQUEST_FORMAT
                        )
                    )
                }
            }

            // Get all properties with filtering and pagination: GET /api/properties
            get {
                val type = call.request.queryParameters["type"]
                val location = call.request.queryParameters["location"]
                val minPrice = call.request.queryParameters["minPrice"]?.toIntOrNull()
                val maxPrice = call.request.queryParameters["maxPrice"]?.toIntOrNull()
                val minSize = call.request.queryParameters["minSize"]?.toDoubleOrNull()
                val maxSize = call.request.queryParameters["maxSize"]?.toDoubleOrNull()
                val isAvailable =
                    call.request.queryParameters["isAvailable"]?.toBooleanStrictOrNull()
                val propertyOwnerId = call.request.queryParameters["ownerId"]
                val amenities = call.request.queryParameters.getAll("amenities")
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 1
                val pageSize = call.request.queryParameters["pageSize"]?.toIntOrNull()
                    ?: Constants.Defaults.PAGE_SIZE

                val filter = PropertyFilter(
                    type = type,
                    location = location,
                    minPrice = minPrice,
                    maxPrice = maxPrice,
                    minSize = minSize,
                    maxSize = maxSize,
                    isAvailable = isAvailable,
                    amenities = amenities,
                    propertyOwnerId = propertyOwnerId
                )

                propertyService.getAllProperties(filter, page, pageSize)
                    .onSuccess { response ->
                        call.respond(
                            HttpStatusCode.OK, ApiResponse(
                                success = true,
                                data = response
                            )
                        )
                    }
                    .onFailure { error ->
                        call.respond(
                            HttpStatusCode.InternalServerError, ApiResponse<Nothing>(
                                success = false,
                                error = error.message
                            )
                        )
                    }
            }

            // Search properties: GET /api/properties/search?q=query
            get(Constants.Endpoints.SEARCH) {
                val query = call.request.queryParameters["q"]
                if (query.isNullOrBlank()) {
                    call.respond(
                        HttpStatusCode.BadRequest, ApiResponse<Nothing>(
                            success = false,
                            error = Constants.ErrorMessages.SEARCH_QUERY_REQUIRED
                        )
                    )
                    return@get
                }

                propertyService.searchProperties(query)
                    .onSuccess { properties ->
                        call.respond(
                            HttpStatusCode.OK, ApiResponse(
                                success = true,
                                data = properties
                            )
                        )
                    }
                    .onFailure { error ->
                        call.respond(
                            HttpStatusCode.InternalServerError, ApiResponse<Nothing>(
                                success = false,
                                error = error.message
                            )
                        )
                    }
            }

            get("/{id}") {
                val id = call.parameters["id"] ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponse<Nothing>(
                        success = false,
                        error = Constants.ErrorMessages.INVALID_PROPERTY_ID
                    )
                )

                propertyService.getProperty(id)
                    .onSuccess { property ->
                        call.respond(
                            HttpStatusCode.OK, ApiResponse(
                                success = true,
                                data = property
                            )
                        )
                    }
                    .onFailure { error ->
                        call.respond(
                            HttpStatusCode.NotFound, ApiResponse<Nothing>(
                                success = false,
                                error = error.message
                            )
                        )
                    }
            }

            // Update property: PUT /api/properties/{id}
            put("/{id}") {
                val id = call.parameters["id"] ?: return@put call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponse<Nothing>(
                        success = false,
                        error = Constants.ErrorMessages.INVALID_PROPERTY_ID
                    )
                )

                try {
                    val request = call.receive<UpdatePropertyRequest>()
                    propertyService.updateProperty(id, request)
                        .onSuccess { property ->
                            call.respond(
                                HttpStatusCode.OK, ApiResponse(
                                    success = true,
                                    message = Constants.SuccessMessages.PROPERTY_UPDATED,
                                    data = property
                                )
                            )
                        }
                        .onFailure { error ->
                            call.respond(
                                HttpStatusCode.BadRequest, ApiResponse<Nothing>(
                                    success = false,
                                    error = error.message
                                )
                            )
                        }
                } catch (_: Exception) {
                    call.respond(
                        HttpStatusCode.BadRequest, ApiResponse<Nothing>(
                            success = false,
                            error = Constants.ErrorMessages.INVALID_REQUEST_FORMAT
                        )
                    )
                }
            }

            // Update property availability: PATCH /api/properties/{id}/availability?available=true
            patch("/{id}${Constants.Endpoints.AVAILABILITY}") {
                val id = call.parameters["id"] ?: return@patch call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponse<Nothing>(
                        success = false,
                        error = Constants.ErrorMessages.INVALID_PROPERTY_ID
                    )
                )

                val isAvailable = call.request.queryParameters["available"]?.toBooleanStrictOrNull()
                    ?: return@patch call.respond(
                        HttpStatusCode.BadRequest,
                        ApiResponse<Nothing>(
                            success = false,
                            error = Constants.ErrorMessages.AVAILABILITY_PARAM_REQUIRED
                        )
                    )

                propertyService.updatePropertyAvailability(id, isAvailable)
                    .onSuccess {
                        call.respond(
                            HttpStatusCode.OK, ApiResponse<Unit>(
                                success = true,
                                message = Constants.SuccessMessages.AVAILABILITY_UPDATED
                            )
                        )
                    }
                    .onFailure { error ->
                        call.respond(
                            HttpStatusCode.NotFound, ApiResponse<Nothing>(
                                success = false,
                                error = error.message
                            )
                        )
                    }
            }

            // Delete property: DELETE /api/properties/{id}
            delete("/{id}") {
                val id = call.parameters["id"] ?: return@delete call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponse<Nothing>(
                        success = false,
                        error = Constants.ErrorMessages.INVALID_PROPERTY_ID
                    )
                )

                propertyService.deleteProperty(id)
                    .onSuccess {
                        call.respond(
                            HttpStatusCode.OK, ApiResponse<Unit>(
                                success = true,
                                message = Constants.SuccessMessages.PROPERTY_DELETED
                            )
                        )
                    }
                    .onFailure { error ->
                        call.respond(
                            HttpStatusCode.NotFound, ApiResponse<Nothing>(
                                success = false,
                                error = error.message
                            )
                        )
                    }
            }

            // Get properties by owner: GET /api/properties/owner/{ownerId}
            get("${Constants.Endpoints.OWNER}/{ownerId}") {
                val ownerId = call.parameters["ownerId"] ?: return@get call.respond(
                    HttpStatusCode.BadRequest,
                    ApiResponse<Nothing>(
                        success = false,
                        error = Constants.ErrorMessages.INVALID_OWNER_ID
                    )
                )

                propertyService.getPropertiesByOwner(ownerId)
                    .onSuccess { properties ->
                        call.respond(
                            HttpStatusCode.OK, ApiResponse(
                                success = true,
                                data = properties
                            )
                        )
                    }
                    .onFailure { error ->
                        call.respond(
                            HttpStatusCode.InternalServerError, ApiResponse<Nothing>(
                                success = false,
                                error = error.message
                            )
                        )
                    }
            }
        }
    }
}