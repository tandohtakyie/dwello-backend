package utils

object AppEnvironment {
    val MONGODB_URI: String by lazy { System.getenv("MONGODB_URI") ?: throw IllegalStateException("MONGODB_URI not found") }
    val MONGODB_DB_NAME: String by lazy { System.getenv("MONGODB_DB_NAME") ?: throw IllegalStateException("MONGODB_DB_NAME not found") }
    val JWT_SECRET: String by lazy { System.getenv("JWT_SECRET") ?: throw IllegalStateException("JWT_SECRET not found") }
    val JWT_ISSUER: String by lazy { System.getenv("JWT_ISSUER") ?: "default-ktor-app-issuer" }
    val JWT_REALM: String by lazy { System.getenv("JWT_REALM") ?: "ktor-app-access" }
    val JWT_EXPIRATION_HOURS: Long by lazy { System.getenv("JWT_EXPIRATION_HOURS")?.toLongOrNull() ?: 24L }
    val SERVER_PORT: Int by lazy { System.getenv("PORT")?.toIntOrNull() ?: 8080 }

    fun loadAndValidate() { // Call this at startup to fail fast if required vars are missing
        MONGODB_URI
        MONGODB_DB_NAME
        JWT_SECRET
        // JWT_ISSUER, JWT_REALM, JWT_EXPIRATION_HOURS have defaults, so no need to fail for them here
        // SERVER_PORT has a default
        println("Successfully loaded and validated environment variables.")
    }
}