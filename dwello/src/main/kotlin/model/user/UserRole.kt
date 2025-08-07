package model.user

import kotlinx.serialization.Serializable

@Serializable
enum class UserRole {
    BUYER_RENT,
    PROPERTY_OWNER,
    ADMIN,
}