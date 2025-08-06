package repository

import dto.CreatePropertyRequest
import dto.PropertyFilter
import dto.UpdatePropertyRequest
import model.Property
import utils.Constants

/**
 * Repository interface for property data access operations
 */
interface PropertyRepository {
    suspend fun createProperty(request: CreatePropertyRequest): Property
    suspend fun findPropertyById(id: String): Property?
    suspend fun findAllProperties(
        filter: PropertyFilter = PropertyFilter(),
        page: Int = 1,
        pageSize: Int = Constants.Defaults.PAGE_SIZE
    ): Pair<List<Property>, Long>
    suspend fun updateProperty(id: String, request: UpdatePropertyRequest): Property?
    suspend fun deleteProperty(id: String): Boolean
    suspend fun findPropertyByOwnerId(ownerId: String): List<Property>
    suspend fun updatePropertyAvailability(id: String, isAvailable: Boolean): Boolean
    suspend fun searchProperties(query: String): List<Property>
}