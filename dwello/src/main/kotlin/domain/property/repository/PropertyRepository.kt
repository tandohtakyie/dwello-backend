package domain.property.repository

import domain.property.model.Property

interface PropertyRepository {
    suspend fun getAvailableProperties(): List<Property>
    suspend fun getPropertyById(id: String): Property?
    suspend fun addProperty(property: Property): Boolean
    suspend fun updateAvailability(id: String, available: Boolean): Boolean
}