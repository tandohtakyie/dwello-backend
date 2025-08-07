package service.property

import dto.property.CreatePropertyRequest
import dto.property.PropertyResponse
import dto.property.UpdatePropertyRequest
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import model.property.Property
import model.user.UserRole
import repository.property.PropertyRepository
import repository.user.UserRepository

class PropertyServiceImpl(
    private val propertyRepository: PropertyRepository,
    private val userRepository: UserRepository, // Needed for favorites
) : PropertyService {

    private suspend fun Property.toResponse(currentUserId: String?): PropertyResponse {
        val isFavorited = if (currentUserId != null) {
            userRepository.getUserFavoritePropertyIds(currentUserId).contains(this.id)
        } else {
            false
        }
        return PropertyResponse(
            id = this.id,
            name = this.name,
            type = this.type,
            listingType = this.listingType,
            description = this.description,
            price = this.price,
            location = this.location,
            isAvailable = this.isAvailable,
            sizeInSquareMeters = this.sizeInSquareMeters,
            images = this.images,
            amenities = this.amenities,
            propertyOwnerId = this.propertyOwnerId,
            createdAt = this.createdAt.toString(),
            updatedAt = this.updatedAt.toString(),
            leaseTerms = this.leaseTerms,
            saleTerms = this.saleTerms,
            rating = this.rating,
            isFavorited = isFavorited
        )
    }

    override suspend fun createProperty(
        userId: String,
        userRole: UserRole,
        request: CreatePropertyRequest
    ): Result<PropertyResponse> {
        if (userRole != UserRole.PROPERTY_OWNER) {
            return Result.failure(Exception("Only property owners can create property listing"))
        }
        val now = Clock.System.now().toLocalDateTime(TimeZone.UTC)
        val property = Property(
            name = request.name,
            type = request.type,
            listingType = request.listingType,
            description = request.description,
            price = request.price,
            location = request.location,
            propertyOwnerId = userId, // Set from authenticated user
            createdAt = now,
            updatedAt = now,
            images = request.images,
            amenities = request.amenities,
            leaseTerms = request.leaseTerms,
            saleTerms = request.saleTerms,
            sizeInSquareMeters = request.sizeInSquareMeters,
        )

        val createdProperty = propertyRepository.createProperty(property)
        return if (createdProperty != null) {
            Result.success(createdProperty.toResponse(userId))
        } else {
            Result.failure(RuntimeException("Failed to create property"))
        }
    }

    override suspend fun getPropertyById(
        propertyId: String,
        currentUserId: String?
    ): Result<PropertyResponse> {
        val property = propertyRepository.findPropertyById(propertyId)
        return if (property != null) {
            Result.success(property.toResponse(currentUserId))
        } else {
            Result.failure(NoSuchElementException("Property not found"))
        }
    }

    override suspend fun getAllProperties(
        page: Int,
        pageSize: Int,
        currentUserId: String?
    ): Result<List<PropertyResponse>> {
        val (properties, _) = propertyRepository.findAllProperties(null, page, pageSize)
        val response = properties.map { it.toResponse(currentUserId) }
        return Result.success(response)
    }

    override suspend fun updateProperty(
        propertyId: String,
        ownerId: String,
        request: UpdatePropertyRequest
    ): Result<PropertyResponse> {
        val existingProperty = propertyRepository.findPropertyById(propertyId)
            ?: return Result.failure(NoSuchElementException("Property not found"))

        if (existingProperty.propertyOwnerId != ownerId) {
            return Result.failure(SecurityException("You are not the owner of this property and are not authorized to update it."))
        }

        val updatesMap = mutableMapOf<String, Any?>()
        request.name?.let { updatesMap["name"] = it }
        request.type?.let { updatesMap["type"] = it }
        request.listingType?.let { updatesMap["listingType"] = it }
        //TODO ... add all other updatable fields from UpdatePropertyRequest
        request.price?.let { updatesMap["price"] = it }
        request.isAvailable?.let { updatesMap["isAvailable"] = it }

        if (updatesMap.isEmpty()) return Result.success(existingProperty.toResponse(ownerId))
        val updatedProperty = propertyRepository.updateProperty(propertyId, updatesMap)
        return if (updatedProperty != null) {
            Result.success(updatedProperty.toResponse(ownerId))
        } else {
            Result.failure(RuntimeException("Failed to update property."))
        }
    }

    override suspend fun deleteProperty(
        propertyId: String,
        ownerId: String
    ): Result<Unit> {
        val existingProperty = propertyRepository.findPropertyById(propertyId)
            ?: return Result.failure(NoSuchElementException("Property not found."))

        if (existingProperty.propertyOwnerId != ownerId) {
            return Result.failure(SecurityException("You are not authorized to delete this property."))
        }
        return if (propertyRepository.deleteProperty(propertyId)) {
            Result.success(Unit)
        } else {
            Result.failure(RuntimeException("Failed to delete property."))
        }
    }

    override suspend fun getPropertiesByOwner(
        ownerId: String,
        currentUserId: String?
    ): Result<List<PropertyResponse>> {
        //TODO make sure this is about the properties added by the owner with the owner id
        // Here, currentUserId is for determining 'isFavorited' status if the owner themselves are browsing
        val properties = propertyRepository.findPropertiesByOwnerId(ownerId)
        val responses = properties.map { it.toResponse(currentUserId) }
        return Result.success(responses)
    }

    override suspend fun addPropertyToFavorites(
        userId: String,
        propertyId: String
    ): Result<Unit> {
        // Check if property exists before favoriting
        if (propertyRepository.findPropertyById(propertyId) == null) {
            return Result.failure(NoSuchElementException("Property not found with ID: $propertyId"))
        }
        // Check if user exists (though typically user would be authenticated)
        if (userRepository.findUserById(userId) == null) {
            return Result.failure(NoSuchElementException("User not found with ID: $userId"))
        }

        return if (userRepository.addPropertyToFavorites(userId, propertyId)) {
            Result.success(Unit)
        } else {
            // This could also mean it was already favorited if using addToSet and no modification occurred
            // Or a database error. More specific error handling could be added.
            Result.failure(RuntimeException("Failed to add property to favorites or already favorited."))
        }
    }

    override suspend fun removePropertyFromFavorites(
        userId: String,
        propertyId: String
    ): Result<Unit> {
        // Check if property and user exist (optional, for robustness)
        if (propertyRepository.findPropertyById(propertyId) == null) {
            return Result.failure(NoSuchElementException("Property not found."))
        }
        if (userRepository.findUserById(userId) == null) {
            return Result.failure(NoSuchElementException("User not found."))
        }

        return if (userRepository.removePropertyFromFavorites(userId, propertyId)) {
            Result.success(Unit)
        } else {
            Result.failure(RuntimeException("Failed to remove property from favorites or not found in favorites."))
        }
    }

    override suspend fun getUserFavoriteProperties(userId: String): Result<List<PropertyResponse>> {
        val favoriteIds = userRepository.getUserFavoritePropertyIds(userId)
        if (favoriteIds.isEmpty()) {
            return Result.success(emptyList())
        }
        val properties = propertyRepository.findPropertiesByIds(favoriteIds)
        // For favorites, they are inherently favorited by this user
        val responses = properties.map { it.toResponse(currentUserId = userId) }
        return Result.success(responses)
    }
}