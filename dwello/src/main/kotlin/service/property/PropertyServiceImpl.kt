package service.property

import dto.request.property.CreatePropertyRequest
import dto.request.property.PaginatedResponse
import dto.request.property.PropertyFilter
import dto.request.property.UpdatePropertyRequest
import model.property.Property
import repository.property.PropertyRepository
import utils.Constants

class PropertyServiceImpl(
    private val repository: PropertyRepository
) : PropertyService {
    override suspend fun createProperty(request: CreatePropertyRequest): Result<Property> {
        return try {
            validateCreateRequest(request)
            val property = repository.createProperty(request)
            Result.success(property)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getProperty(id: String): Result<Property> {
        return try {
            val property = repository.findPropertyById(id)
                ?: return Result.failure(Exception(Constants.ErrorMessages.PROPERTY_NOT_FOUND))
            Result.success(property)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAllProperties(
        filter: PropertyFilter,
        page: Int,
        pageSize: Int
    ): Result<PaginatedResponse<Property>> {
        return try {
            // Validate pagination parameters
            val validatedPageSize = pageSize.coerceIn(1, Constants.Defaults.MAX_PAGE_SIZE)
            val validatedPage = page.coerceAtLeast(1)

            val (properties, total) = repository.findAllProperties(
                filter,
                validatedPage,
                validatedPageSize
            )
            val totalPages = ((total + validatedPageSize - 1) / validatedPageSize).toInt()

            val response = PaginatedResponse(
                items = properties,
                total = total,
                page = validatedPage,
                pageSize = validatedPageSize,
                totalPages = totalPages
            )
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateProperty(
        id: String,
        request: UpdatePropertyRequest
    ): Result<Property> {
        return try {
            validateUpdateRequest(request)
            val property = repository.updateProperty(id, request)
                ?: return Result.failure(Exception(Constants.ErrorMessages.PROPERTY_NOT_FOUND))
            Result.success(property)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteProperty(id: String): Result<Unit> {
        return try {
            val deleted = repository.deleteProperty(id)
            if (deleted) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(Constants.ErrorMessages.PROPERTY_NOT_FOUND))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPropertiesByOwner(ownerId: String): Result<List<Property>> {
        return try {
            val properties = repository.findPropertyByOwnerId(ownerId)
            Result.success(properties)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updatePropertyAvailability(
        id: String,
        isAvailable: Boolean
    ): Result<Unit> {
        return try {
            val updated = repository.updatePropertyAvailability(id, isAvailable)
            if (updated) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(Constants.ErrorMessages.PROPERTY_NOT_FOUND))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun searchProperties(query: String): Result<List<Property>> {
        return try {
            if (query.isBlank()) {
                return Result.failure(Exception(Constants.ErrorMessages.SEARCH_QUERY_EMPTY))
            }
            val properties = repository.searchProperties(query.trim())
            Result.success(properties)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun validateCreateRequest(request: CreatePropertyRequest) {
        if (request.name.isBlank()) throw Exception(Constants.ErrorMessages.NAME_REQUIRED)
        if (request.type.isBlank()) throw Exception(Constants.ErrorMessages.TYPE_REQUIRED)
        if (request.pricePerMonth < Constants.Validation.MIN_PRICE)
            throw Exception(Constants.ErrorMessages.PRICE_POSITIVE)
        if (request.location.isBlank()) throw Exception(Constants.ErrorMessages.LOCATION_REQUIRED)
        if (request.propertyOwnerId.isBlank()) throw Exception(Constants.ErrorMessages.OWNER_ID_REQUIRED)

        request.sizeInSquareMeters?.let {
            if (it < Constants.Validation.MIN_SIZE) throw Exception(Constants.ErrorMessages.SIZE_POSITIVE)
        }
    }

    private fun validateUpdateRequest(request: UpdatePropertyRequest) {
        request.name?.let {
            if (it.isBlank()) throw Exception(Constants.ErrorMessages.NAME_EMPTY)
        }
        request.type?.let {
            if (it.isBlank()) throw Exception(Constants.ErrorMessages.TYPE_EMPTY)
        }
        request.pricePerMonth?.let {
            if (it < Constants.Validation.MIN_PRICE) throw Exception(Constants.ErrorMessages.PRICE_POSITIVE)
        }
        request.location?.let {
            if (it.isBlank()) throw Exception(Constants.ErrorMessages.LOCATION_EMPTY)
        }
        request.sizeInSquareMeters?.let {
            if (it < Constants.Validation.MIN_SIZE) throw Exception(Constants.ErrorMessages.SIZE_POSITIVE)
        }
        request.rating?.let {
            if (it < Constants.Validation.MIN_RATING || it > Constants.Validation.MAX_RATING)
                throw Exception(Constants.ErrorMessages.RATING_RANGE)
        }
    }
}