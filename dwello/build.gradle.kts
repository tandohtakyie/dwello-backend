plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ktor)
}

group = "com.aimhigh"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

dependencies {
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.logback.classic)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.config.yaml)

    implementation(libs.bundles.ktor.server)
    implementation(libs.bundles.koin)
    implementation(libs.bundles.mongodb)
    implementation(libs.bundles.kotlinx)
    implementation(libs.logback.classic)
    implementation(libs.dotenv.kotlin)


    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)
}
