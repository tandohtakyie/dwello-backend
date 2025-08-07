package model.property

import kotlinx.serialization.Serializable

@Serializable
enum class ListingType {
    FOR_RENT,
    FOR_SALE,
}