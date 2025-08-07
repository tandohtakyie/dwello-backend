package repository.property

import dto.property.CreatePropertyRequest
import dto.property.PropertyFilter
import dto.property.UpdatePropertyRequest
import model.property.Property
import utils.Constants

/**
 * Repository interface for property data access operations
 */
interface PropertyRepository {
    suspend fun createProperty(property: Property): Property?
    suspend fun findPropertyById(id: String): Property?
    suspend fun findAllProperties(filter: PropertyFilter?, page: Int, pageSize: Int): Pair<List<Property>, Long>

    suspend fun updateProperty(id: String, updates: Map<String, Any?>): Property?
    suspend fun deleteProperty(id: String): Boolean
    suspend fun findPropertiesByOwnerId(ownerId: String): List<Property>

    suspend fun findPropertiesByIds(ids: List<String>): List<Property>


    //TODO check these 2 methods if still needed
    suspend fun updatePropertyAvailability(propertyId: String, isAvailable: Boolean): Boolean
    suspend fun searchProperties(query: String, page: Int, pageSize: Int): Pair<List<Property>, Long>
}