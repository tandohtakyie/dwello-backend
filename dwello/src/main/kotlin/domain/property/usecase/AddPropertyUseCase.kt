package domain.property.usecase

import domain.property.model.Property
import domain.property.repository.PropertyRepository

class AddPropertyUseCase(
    private val repository: PropertyRepository
) {
    suspend fun execute(property: Property): Boolean {
        return repository.addProperty(property)
    }
}