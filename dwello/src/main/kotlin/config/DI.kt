package config

import data.property.PropertyRepositoryImpl
import domain.property.repository.PropertyRepository
import domain.property.usecase.AddPropertyUseCase
import domain.property.usecase.GetAvailablePropertiesUseCase
import domain.property.usecase.UpdateAvailabilityUseCase
import org.koin.dsl.module

val propertyModule = module {
    // Repository binding
    single<PropertyRepository> { PropertyRepositoryImpl() }

    factory { GetAvailablePropertiesUseCase(get()) }
    single { AddPropertyUseCase(get()) }
    single { UpdateAvailabilityUseCase(get()) }
}