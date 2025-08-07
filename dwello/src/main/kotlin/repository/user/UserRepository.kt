package repository.user

import model.user.User

interface UserRepository {
    suspend fun createUser(user: User): User?
    suspend fun findUserByEmail(email: String): User?
    suspend fun findUserById(id: String): User?
    suspend fun addPropertyToFavorites(userId: String, propertyId: String): Boolean
    suspend fun removePropertyFromFavorites(userId: String, propertyId: String): Boolean
    suspend fun getUserFavoritePropertyIds(userId: String): List<String>
}