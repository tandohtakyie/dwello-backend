package routes

import io.ktor.server.application.Application
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import org.koin.ktor.ext.inject
import routes.auth.authRoutes
import routes.property.propertyRoutes
import routes.user.userRoutes
import service.property.PropertyService
import service.user.auth.AuthService

fun Application.configureRouting() {
    val authService by inject<AuthService>()
    val propertyService by inject<PropertyService>()

    routing {
        route(path = "/api/v1") {
            authRoutes(authService)
            userRoutes(propertyService)
            propertyRoutes(propertyService)
        }
    }
}