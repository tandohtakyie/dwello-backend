package data.property

import domain.property.model.Property
import domain.property.repository.PropertyRepository
import java.time.LocalDateTime
import kotlin.String

class PropertyRepositoryImpl: PropertyRepository {

    private val properties = listOf(
        Property(
            name = "Cozy Studio",
            type = "Studio",
            description = "A cozy apartment for rent",
            pricePerMonth = 1500,
            location = "123 Main St",
            isAvailable = true,
            sizeInSquareMeters = 30.0,
            images = listOf("https://example.com/studio1.jpg"),
            amenities = listOf("Wi-Fi", "Air Conditioning", "Heating"),
            propertyOwnerId = "owner123",
            createdAt = LocalDateTime.of(2025, 8, 1, 10, 0),
            updatedAt = LocalDateTime.of(2025, 8, 4, 21, 45),
            leaseTerms = "Minimum 3-month lease, utilities excluded",
            rating = 4.5f
        ),
        Property(
            name = "Modern Loft",
            type = "Loft",
            description = "Open-concept loft with high ceilings",
            pricePerMonth = 2300,
            location = "456 Market Ave",
            isAvailable = true,
            sizeInSquareMeters = 65.0,
            images = listOf("https://example.com/loft.jpg"),
            amenities = listOf("Washer/Dryer", "Smart Lock", "Gym Access"),
            propertyOwnerId = "owner234",
            createdAt = LocalDateTime.of(2025, 7, 15, 16, 30),
            updatedAt = LocalDateTime.of(2025, 8, 3, 12, 0),
            leaseTerms = "6-month lease, utilities included",
            rating = 4.8f
        ),
        Property(
            name = "Family Home",
            type = "House",
            description = "Spacious 3-bedroom house with backyard",
            pricePerMonth = 3200,
            location = "789 Oak Dr",
            isAvailable = false,
            sizeInSquareMeters = 120.0,
            images = listOf("https://example.com/house.jpg"),
            amenities = listOf("Garage", "Garden", "Fireplace"),
            propertyOwnerId = "owner345",
            createdAt = LocalDateTime.of(2025, 6, 10, 9, 15),
            updatedAt = LocalDateTime.of(2025, 8, 2, 18, 30),
            leaseTerms = "1-year lease, no pets",
            rating = 4.3f
        ),
        Property(
            name = "Waterfront Condo",
            type = "Condo",
            description = "Luxury condo with ocean view",
            pricePerMonth = 4100,
            location = "11 Ocean Blvd",
            isAvailable = true,
            sizeInSquareMeters = 85.0,
            images = listOf("https://example.com/condo.jpg"),
            amenities = listOf("Pool", "Gym", "24/7 Security"),
            propertyOwnerId = "owner456",
            createdAt = LocalDateTime.of(2025, 7, 20, 14, 50),
            updatedAt = LocalDateTime.of(2025, 8, 4, 20, 10),
            leaseTerms = "Minimum 6-month lease, utilities included",
            rating = 4.9f
        ),
        Property(
            name = "Student Apartment",
            type = "Apartment",
            description = "Affordable option ideal for students",
            pricePerMonth = 900,
            location = "22 Campus Rd",
            isAvailable = true,
            sizeInSquareMeters = 25.0,
            images = listOf("https://example.com/student.jpg"),
            amenities = listOf("Wi-Fi", "Shared Kitchen", "Study Lounge"),
            propertyOwnerId = "owner567",
            createdAt = LocalDateTime.of(2025, 8, 3, 13, 10),
            updatedAt = LocalDateTime.of(2025, 8, 4, 19, 40),
            leaseTerms = "Month-to-month lease, utilities excluded",
            rating = 4.1f
        ),
        Property(
            name = "Penthouse Retreat",
            type = "Penthouse",
            description = "Top-floor retreat with skyline views",
            pricePerMonth = 5200,
            location = "88 Skyline Ave",
            isAvailable = true,
            sizeInSquareMeters = 100.0,
            images = listOf("https://example.com/penthouse.jpg"),
            amenities = listOf("Private Elevator", "Rooftop Deck", "Smart Home Features"),
            propertyOwnerId = "owner678",
            createdAt = LocalDateTime.of(2025, 7, 30, 11, 20),
            updatedAt = LocalDateTime.of(2025, 8, 4, 21, 30),
            leaseTerms = "12-month lease, utilities included",
            rating = 5.0f
        )
    )


    override suspend fun getAvailableProperties() = properties.filter { it.isAvailable }

    override suspend fun getPropertyById(id: String): Property? {
        TODO("Not yet implemented")
    }

    override suspend fun addProperty(property: Property): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun updateAvailability(
        id: String,
        available: Boolean
    ): Boolean {
        TODO("Not yet implemented")
    }
}