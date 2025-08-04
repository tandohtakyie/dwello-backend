package domain.property.usecase

import domain.property.repository.PropertyRepository

class UpdateAvailabilityUseCase(
    private val repository: PropertyRepository
) {
    suspend fun execute(propertyId: String, available: Boolean) {
        repository.updateAvailability(propertyId, available)
    }
}
