package di

import config.DatabaseConfig
import config.DatabaseConfigImpl
import org.koin.dsl.module
import repository.property.PropertyRepository
import repository.property.PropertyRepositoryImpl
import service.property.PropertyService
import service.property.PropertyServiceImpl

val appModule = module {
    single<DatabaseConfig> { DatabaseConfigImpl() }
    single<PropertyRepository> { PropertyRepositoryImpl(get()) }
    single<PropertyService> { PropertyServiceImpl(get()) }
}