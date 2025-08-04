package domain.property.usecase

import domain.property.repository.PropertyRepository
import domain.property.model.Property

class GetAvailablePropertiesUseCase(
    private val repository: PropertyRepository
) {
    suspend fun execute(): List<Property> = repository.getAvailableProperties()
}