package app

import config.configModule
import config.mongoModule
import config.propertyModule
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.koin.ktor.ext.get
import org.koin.ktor.plugin.Koin

fun main(args: Array<String>) {
    embeddedServer(factory = Netty, port = 8080) {
        module()
    }.start(wait = true)
}

fun Application.module() {
    install(plugin = Koin) {
        modules(configModule, mongoModule, propertyModule)
    }
    configureRouting(
        getAvailablePropertiesUseCase = get(),
        addPropertyUseCase = get(),
        updateAvailabilityUseCase = get()
    )
}
