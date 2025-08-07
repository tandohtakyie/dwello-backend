package dto.user

import kotlinx.serialization.Serializable

@Serializable
data class FavoriteRequest(
    val propertyId: String,
)
