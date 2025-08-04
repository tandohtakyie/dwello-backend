package app

import domain.property.usecase.AddPropertyUseCase
import domain.property.usecase.GetAvailablePropertiesUseCase
import domain.property.usecase.UpdateAvailabilityUseCase
import io.ktor.server.application.Application
import io.ktor.server.routing.routing
import presentation.controller.propertyRoute

fun Application.configureRouting(
    getAvailablePropertiesUseCase: GetAvailablePropertiesUseCase,
    addPropertyUseCase: AddPropertyUseCase,
    updateAvailabilityUseCase: UpdateAvailabilityUseCase,
) {
    routing {
        propertyRoute(
            getAvailablePropertiesUseCase = getAvailablePropertiesUseCase,
            addPropertyUseCase = addPropertyUseCase,
            updateAvailabilityUseCase = updateAvailabilityUseCase,
        )
    }
}
