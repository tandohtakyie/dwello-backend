package app

import com.mongodb.kotlin.client.coroutine.MongoDatabase
import domain.property.usecase.AddPropertyUseCase
import domain.property.usecase.GetAvailablePropertiesUseCase
import domain.property.usecase.UpdateAvailabilityUseCase
import io.ktor.server.application.Application
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import org.bson.Document
import org.koin.ktor.ext.get
import presentation.controller.propertyRoute

fun Application.configureRouting(
    getAvailablePropertiesUseCase: GetAvailablePropertiesUseCase,
    addPropertyUseCase: AddPropertyUseCase,
    updateAvailabilityUseCase: UpdateAvailabilityUseCase,
) {
    val database: MongoDatabase = get()
    routing {
        get("/ping-mongo") {
            try {
                val result = database.runCommand(Document("ping", 1))
                call.respondText("✅ MongoDB is connected!\nResult: $result")
            } catch (e: Exception) {
                call.respondText("❌ Connection failed: ${e.message}")
            }
        }
        propertyRoute(
            getAvailablePropertiesUseCase = getAvailablePropertiesUseCase,
            addPropertyUseCase = addPropertyUseCase,
            updateAvailabilityUseCase = updateAvailabilityUseCase,
        )
    }
}
