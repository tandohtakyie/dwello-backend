package config

import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import io.github.cdimascio.dotenv.dotenv
import utils.Constants

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
        MongoClient.create(connectionString)
    }

    override val database: MongoDatabase by lazy {
        client.getDatabase(databaseName)
    }

    override fun close() {
        client.close()
    }
}