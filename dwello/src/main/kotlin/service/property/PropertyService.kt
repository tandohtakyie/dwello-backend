package service.property

import dto.property.CreatePropertyRequest
import dto.property.PaginatedResponse
import dto.property.PropertyFilter
import dto.property.PropertyResponse
import dto.property.UpdatePropertyRequest
import model.property.Property
import model.user.UserRole
import utils.Constants

interface PropertyService {

    suspend fun createProperty(
        userId: String,
        userRole: UserRole,
        request: CreatePropertyRequest
    ): Result<PropertyResponse>

    suspend fun getPropertyById(
        propertyId: String,
        currentUserId: String?
    ): Result<PropertyResponse>

    suspend fun getAllProperties(
        page: Int,
        pageSize: Int,
        currentUserId: String? /*, filters */
    ): Result<List<PropertyResponse>> // And total

    suspend fun updateProperty(
        propertyId: String,
        ownerId: String,
        request: UpdatePropertyRequest
    ): Result<PropertyResponse>

    suspend fun deleteProperty(propertyId: String, ownerId: String): Result<Unit>

    // *** NEW METHODS ***
    suspend fun getPropertiesByOwner(
        ownerId: String,
        currentUserId: String?
    ): Result<List<PropertyResponse>>

    suspend fun addPropertyToFavorites(userId: String, propertyId: String): Result<Unit>
    suspend fun removePropertyFromFavorites(userId: String, propertyId: String): Result<Unit>
    suspend fun getUserFavoriteProperties(userId: String): Result<List<PropertyResponse>>
}