package di

import config.DatabaseConfig
import config.DatabaseConfigImpl
import org.koin.dsl.module
import repository.PropertyRepository
import repository.PropertyRepositoryImpl
import service.PropertyService
import service.PropertyServiceImpl

val appModule = module {
    single<DatabaseConfig> { DatabaseConfigImpl() }
    single<PropertyRepository> { PropertyRepositoryImpl(get()) }
    single<PropertyService> { PropertyServiceImpl(get()) }
}