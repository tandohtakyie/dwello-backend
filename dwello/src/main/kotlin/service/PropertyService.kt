package service

import dto.CreatePropertyRequest
import dto.PaginatedResponse
import dto.PropertyFilter
import dto.UpdatePropertyRequest
import model.Property
import utils.Constants

interface PropertyService {
    suspend fun createProperty(request: CreatePropertyRequest): Result<Property>
    suspend fun getProperty(id: String): Result<Property>
    suspend fun getAllProperties(
        filter: PropertyFilter = PropertyFilter(),
        page: Int = 1,
        pageSize: Int = Constants.Defaults.PAGE_SIZE
    ): Result<PaginatedResponse<Property>>
    suspend fun updateProperty(id: String, request: UpdatePropertyRequest): Result<Property>
    suspend fun deleteProperty(id: String): Result<Unit>
    suspend fun getPropertiesByOwner(ownerId: String): Result<List<Property>>
    suspend fun updatePropertyAvailability(id: String, isAvailable: Boolean): Result<Unit>
    suspend fun searchProperties(query: String): Result<List<Property>>
}