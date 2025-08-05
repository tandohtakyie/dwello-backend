package config

import com.typesafe.config.ConfigFactory
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.config.HoconApplicationConfig
import org.koin.dsl.module

data class MongoConfig(
    val uri: String,
    val db: String,
)

val configModule = module {
    single {
        val config: ApplicationConfig = HoconApplicationConfig(ConfigFactory.load())
        val uri = config.property(path = "dwello.mongo.uri").getString()
        val db = config.property(path = "dwello.mongo.db").getString()
        MongoConfig(uri, db)
    }
}