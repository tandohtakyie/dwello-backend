package config

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import io.github.cdimascio.dotenv.dotenv
import org.bson.codecs.configuration.CodecRegistries
import org.bson.codecs.configuration.CodecRegistry
import org.bson.codecs.pojo.PojoCodecProvider
import utils.Constants
import utils.MongoLocalDateTimeCodec

class DatabaseConfigImpl : DatabaseConfig {

    // Load environment variables from .env
    private val dotenv = dotenv {
        ignoreIfMissing = true // Don't fail if .env file doesn't exist (for production)
    }

    // Get connection string from environment or use default
    private val connectionString = dotenv[Constants.Environment.MONGODB_CONNECTION_STRING]
        ?: Constants.Defaults.CONNECTION_STRING

    // Get database name from environment or use default
    private val databaseName = dotenv[Constants.Environment.DATABASE_NAME]
        ?: Constants.Defaults.DATABASE_NAME

    override val client: MongoClient by lazy {
        val pojoCodecProvider = PojoCodecProvider.builder().automatic(true).build()

        val localDateTimeCodec = MongoLocalDateTimeCodec()

        val customCodecRegistry: CodecRegistry = CodecRegistries.fromRegistries(
            MongoClientSettings.getDefaultCodecRegistry(), // Default codecs for primitives, etc.
            CodecRegistries.fromCodecs(localDateTimeCodec), // Your custom LocalDateTime codec
            CodecRegistries.fromProviders(pojoCodecProvider) // For your 'Property' class and others
        )

        val settings = MongoClientSettings.builder()
            .applyConnectionString(ConnectionString(connectionString))
            .codecRegistry(customCodecRegistry) // Apply the new registry
            .build()

        MongoClient.create(settings)
    }

    override val database: MongoDatabase by lazy {
        client.getDatabase(databaseName)
    }

    override fun close() {
        client.close()
    }
}