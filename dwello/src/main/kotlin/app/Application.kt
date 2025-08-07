package app

import config.DatabaseConfig
import di.appModule
import dto.property.ApiResponse
import io.github.cdimascio.dotenv.dotenv
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStopping
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import kotlinx.serialization.json.Json
import org.koin.ktor.ext.inject
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger
import routes.property.configurePropertyRoutes
import utils.Constants

fun main(args: Array<String>) {

    // Load environment variables
    val dotenv = dotenv {
        ignoreIfMissing = true
    }

    val host = dotenv[Constants.Environment.SERVER_HOST] ?: Constants.Defaults.SERVER_HOST
    val port = (dotenv[Constants.Environment.SERVER_PORT] ?: Constants.Defaults.SERVER_PORT).toInt()

    embeddedServer(factory = Netty, port = port, host = host, module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureKoin()
    configureContentNegotiation()
    configureCORS()
    configureStatusPages()
    configurePropertyRoutes()
    configureShutdownHook()
}


fun Application.configureKoin() {
    install(Koin) {
        slf4jLogger() // Use SLF4J logger for Koin
        modules(appModule)
    }
}

/**
 * Configure JSON content negotiation
 */
fun Application.configureContentNegotiation() {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }
}

/**
 * Configure CORS for cross-origin requests
 */
fun Application.configureCORS() {
    install(CORS) {
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Patch)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Options)
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.AccessControlAllowOrigin)
        allowCredentials = true
        anyHost() // For development - restrict in production
    }
}

/**
 * Configure global exception handling
 */
fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respond(
                HttpStatusCode.InternalServerError,
                ApiResponse<Nothing>(
                    success = false,
                    error = "${Constants.ErrorMessages.INTERNAL_SERVER_ERROR}: ${cause.message}"
                )
            )
        }

        // Handle specific HTTP exceptions
        status(HttpStatusCode.NotFound) { call, _ ->
            call.respond(
                HttpStatusCode.NotFound,
                ApiResponse<Nothing>(
                    success = false,
                    error = "Endpoint not found"
                )
            )
        }

        status(HttpStatusCode.MethodNotAllowed) { call, _ ->
            call.respond(
                HttpStatusCode.MethodNotAllowed,
                ApiResponse<Nothing>(
                    success = false,
                    error = "Method not allowed"
                )
            )
        }
    }
}

/**
 * Configure application shutdown hook to properly close database connections
 */
fun Application.configureShutdownHook() {
    environment.monitor.subscribe(ApplicationStopping) {
        val databaseConfig by inject<DatabaseConfig>()
        databaseConfig.close()
    }
}

