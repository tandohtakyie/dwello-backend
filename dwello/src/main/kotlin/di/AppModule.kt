package di

import auth.JwtConfig
import auth.PasswordHasher
import config.DatabaseConfig
import config.DatabaseConfigImpl
import org.koin.dsl.module
import repository.property.PropertyRepository
import repository.property.PropertyRepositoryImpl
import repository.user.UserRepository
import repository.user.UserRepositoryImpl
import service.property.PropertyService
import service.property.PropertyServiceImpl
import service.user.auth.AuthService

val appModule = module {
    single<DatabaseConfig> { DatabaseConfigImpl() }
    single {
        val secret = getProperty<String>("JWT_SECRET")
        JwtConfig.getInstance(secret)
    }
    single { PasswordHasher } // the object

    single<UserRepository> { UserRepositoryImpl(get()) }
    single<PropertyRepository> { PropertyRepositoryImpl(get()) }

    single { AuthService(get(), get(), get()) }
    single<PropertyService> { PropertyServiceImpl(get(), get()) }
}