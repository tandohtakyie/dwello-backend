package config

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.ServerApi
import com.mongodb.ServerApiVersion
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import org.koin.dsl.module

val mongoModule = module {
    single {
        val config = get<MongoConfig>()
        val connectionString = ConnectionString(config.uri)

        val settings = MongoClientSettings.builder()
            .applyConnectionString(connectionString)
            .serverApi(ServerApi.builder().version(ServerApiVersion.V1).build())
            .build()

        MongoClient.create(settings)
    }

    single<MongoDatabase> {
        val client: MongoClient = get()
        val config = get<MongoConfig>()
        client.getDatabase(config.db)
    }
}