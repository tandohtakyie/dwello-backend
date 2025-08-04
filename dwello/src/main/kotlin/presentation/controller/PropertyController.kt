package presentation.controller

import domain.property.model.Property
import domain.property.usecase.AddPropertyUseCase
import domain.property.usecase.GetAvailablePropertiesUseCase
import domain.property.usecase.UpdateAvailabilityUseCase
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route

fun Route.propertyRoute(
    getAvailablePropertiesUseCase: GetAvailablePropertiesUseCase,
    addPropertyUseCase: AddPropertyUseCase,
    updateAvailabilityUseCase: UpdateAvailabilityUseCase
) {
    route(path = "/properties") {
        get("/available") {
            val availableProperties = getAvailablePropertiesUseCase.execute()
            call.respond(message = availableProperties)
        }

        post {
            val property = call.receive<Property>()
            val result = addPropertyUseCase.execute(property)
            call.respond(mapOf("success" to result))
        }

        put(path = "/{id}/availability") {
            val propertyID = call.parameters["id"] ?: return@put call.respondText(
                "Missing ID",
                status = HttpStatusCode.BadRequest
            )
            val requestBody = call.receive<Map<String, Boolean>>()
            val isAvailable = requestBody["isAvailable"] ?: false
            val result =
                updateAvailabilityUseCase.execute(propertyId = propertyID, available = isAvailable)
            call.respond(mapOf("success" to result))
        }
    }
}