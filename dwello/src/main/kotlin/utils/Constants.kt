package utils

object Constants {

    // Environment variable keys
    object Environment {
        const val MONGODB_CONNECTION_STRING = "MONGODB_CONNECTION_STRING"
        const val DATABASE_NAME = "DATABASE_NAME"
        const val SERVER_HOST = "SERVER_HOST"
        const val SERVER_PORT = "SERVER_PORT"
    }

    // Default values
    object Defaults {
        const val CONNECTION_STRING = "mongodb://localhost:27017"
        const val DATABASE_NAME = "real_estate_db"
        const val SERVER_HOST = "0.0.0.0"
        const val SERVER_PORT = "8080"
        const val PAGE_SIZE = 10
        const val MAX_PAGE_SIZE = 100
    }

    // Database collection names
    object Collections {
        const val PROPERTIES = "properties"
        const val USERS = "users"
    }

    // API endpoints
    object Endpoints {
        const val API_BASE = "/api"
        const val PROPERTIES_ENDPOINT = "$API_BASE/properties"
        const val SEARCH = "/search"
        const val AVAILABILITY = "/availability"
        const val OWNER = "/owner"
    }

    // Property types
    object PropertyTypes {
        const val APARTMENT = "apartment"
        const val HOUSE = "house"
        const val CONDO = "condo"
        const val STUDIO = "studio"
        const val VILLA = "villa"
        const val COMMERCIAL = "commercial"
    }

    // Error messages
    object ErrorMessages {
        const val PROPERTY_NOT_FOUND = "Property not found"
        const val INVALID_PROPERTY_ID = "Property ID is required"
        const val INVALID_OWNER_ID = "Owner ID is required"
        const val INVALID_REQUEST_FORMAT = "Invalid request format"
        const val INTERNAL_SERVER_ERROR = "Internal server error"
        const val SEARCH_QUERY_REQUIRED = "Search query is required"
        const val SEARCH_QUERY_EMPTY = "Search query cannot be empty"
        const val AVAILABILITY_PARAM_REQUIRED = "Available parameter is required"

        // Validation messages
        const val NAME_REQUIRED = "Property name is required"
        const val NAME_EMPTY = "Property name cannot be empty"
        const val TYPE_REQUIRED = "Property type is required"
        const val TYPE_EMPTY = "Property type cannot be empty"
        const val PRICE_POSITIVE = "Price per month must be positive"
        const val LOCATION_REQUIRED = "Location is required"
        const val LOCATION_EMPTY = "Location cannot be empty"
        const val OWNER_ID_REQUIRED = "Property owner ID is required"
        const val SIZE_POSITIVE = "Size must be positive"
        const val RATING_RANGE = "Rating must be between 0 and 5"

        // Authentication messages
        const val JWT_CONFIG_NOT_INITIALIZED = "JwtConfig not initialized"
    }

    // Success messages
    object SuccessMessages {
        const val PROPERTY_CREATED = "Property created successfully"
        const val PROPERTY_UPDATED = "Property updated successfully"
        const val PROPERTY_DELETED = "Property deleted successfully"
        const val AVAILABILITY_UPDATED = "Property availability updated successfully"
    }

    // Validation constants
    object Validation {
        const val MIN_RATING = 0f
        const val MAX_RATING = 5f
        const val MIN_PRICE = 1
        const val MIN_SIZE = 0.1
    }

    object Authentication {
        const val USER_ROLE = "user"
        const val ADMIN_ROLE = "admin"
        const val AUTHENTICATION = "Authentication"
        const val USER_ID = "userId"
        const val ROLE = "role"
        const val EMAIL = "email"
        const val APP_ISSUER = "dwello-backend"



    }
}