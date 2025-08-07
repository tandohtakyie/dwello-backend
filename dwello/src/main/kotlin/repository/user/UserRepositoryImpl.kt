package repository.user

import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.IndexOptions
import com.mongodb.client.model.Indexes
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoCollection
import config.DatabaseConfig
import kotlinx.coroutines.flow.firstOrNull
import model.user.User
import utils.Constants.Authentication.EMAIL
import utils.Constants.Collections.USERS

class UserRepositoryImpl(
    databaseConfig: DatabaseConfig,
) : UserRepository {
    private val userCollection: MongoCollection<User> =
        databaseConfig.database.getCollection(USERS)

    /**
     * Initializes the repository, e.g., creating necessary indexes.
     * Call this at application startup.
     */
    suspend fun init() {
        // Create a unique index on the 'email' field to prevent duplicate emails
        val emailIndexOptions = IndexOptions().unique(true)
        userCollection.createIndex(Indexes.ascending(EMAIL), emailIndexOptions)
    }

    override suspend fun createUser(user: User): User? {
        // Check if user with this email already exists to prevent duplicates due to race conditions
        // even though there's a unique index.
        if (findUserByEmail(user.email) != null) {
            return null // Or throw a specific exception
        }
        val result = userCollection.insertOne(user)
        return if (result.wasAcknowledged()) user.copy(id = user.id) else null
    }

    override suspend fun findUserByEmail(email: String): User? {
        return userCollection.find(eq("email", email.lowercase())).firstOrNull()
    }

    override suspend fun findUserById(id: String): User? {
        return userCollection.find(eq("_id", id))
            .firstOrNull() //TODO check if this is correct with the database

    }

    override suspend fun addPropertyToFavorites(
        userId: String,
        propertyId: String
    ): Boolean {
        val result = userCollection.updateOne(
            eq("_id", userId),
            Updates.addToSet("favoritePropertyIds", propertyId) // addToSet prevents duplicates
        )
        return result.modifiedCount > 0
    }

    override suspend fun removePropertyFromFavorites(
        userId: String,
        propertyId: String
    ): Boolean {
        val result = userCollection.updateOne(
            eq("_id", userId),
            Updates.pull("favoritePropertyIds", propertyId)
        )
        return result.modifiedCount > 0
    }

    override suspend fun getUserFavoritePropertyIds(userId: String): List<String> {
        val user = findUserById(userId)
        return user?.favoritePropertyIds ?: emptyList()
    }
}