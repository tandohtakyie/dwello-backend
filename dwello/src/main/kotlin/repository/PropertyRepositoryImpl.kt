package repository

import com.mongodb.client.model.Filters.and
import com.mongodb.client.model.Filters.empty
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.Filters.gte
import com.mongodb.client.model.Filters.`in`
import com.mongodb.client.model.Filters.lte
import com.mongodb.client.model.Filters.or
import com.mongodb.client.model.Filters.regex
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoCollection
import config.DatabaseConfig
import dto.CreatePropertyRequest
import dto.PropertyFilter
import dto.UpdatePropertyRequest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import model.Property
import org.bson.conversions.Bson
import org.bson.types.ObjectId
import utils.Constants

class PropertyRepositoryImpl(
    databaseConfig: DatabaseConfig
) : PropertyRepository {

    private val collection: MongoCollection<Property> = databaseConfig.database.getCollection(
        collectionName = Constants.Collections.PROPERTIES
    )

    override suspend fun createProperty(request: CreatePropertyRequest): Property {
        val now = Clock.System.now().toLocalDateTime(TimeZone.UTC)
        val property = Property(
            name = request.name,
            type = request.type,
            description = request.description,
            pricePerMonth = request.pricePerMonth,
            location = request.location,
            sizeInSquareMeters = request.sizeInSquareMeters,
            images = request.images,
            amenities = request.amenities,
            propertyOwnerId = request.propertyOwnerId,
            createdAt = now,
            updatedAt = now,
            leaseTerms = request.leaseTerms
        )

        collection.insertOne(property)
        return property
    }

    override suspend fun findPropertyById(id: String): Property? {
        if (!ObjectId.isValid(id)) return null
        return collection.find(eq("_id", ObjectId(id))).firstOrNull()
    }

    override suspend fun findAllProperties(
        filter: PropertyFilter,
        page: Int,
        pageSize: Int
    ): Pair<List<Property>, Long> {

        val filters = mutableListOf<Bson>()

        // Build MongoDB filters based on the filter criteria
        filter.type?.let { filters.add(eq("type", it)) }
        filter.location?.let { filters.add(regex("location", ".*$it.*", "i")) }
        filter.minPrice?.let { filters.add(gte("pricePerMonth", it)) }
        filter.maxPrice?.let { filters.add(lte("pricePerMonth", it)) }
        filter.minSize?.let { filters.add(gte("sizeInSquareMeters", it)) }
        filter.maxSize?.let { filters.add(lte("sizeInSquareMeters", it)) }
        filter.isAvailable?.let { filters.add(eq("isAvailable", it)) }
        filter.propertyOwnerId?.let { filters.add(eq("propertyOwnerId", it)) }
        filter.amenities?.let { amenities ->
            if (amenities.isNotEmpty()) {
                filters.add(`in`("amenities", amenities))
            }
        }

        val combinedFilter = if (filters.isNotEmpty()) and(filters) else empty()

        // Calculate pagination
        val skip = (page - 1) * pageSize
        val properties = collection.find(combinedFilter).skip(skip).limit(pageSize).toList()

        val total = collection.countDocuments(combinedFilter)

        return Pair(properties, total)
    }

    override suspend fun updateProperty(
        id: String,
        request: UpdatePropertyRequest
    ): Property? {
        if (!ObjectId.isValid(id)) return null

        val updates = mutableListOf<Bson>()

        request.name?.let { updates.add(Updates.set("name", it)) }
        request.type?.let { updates.add(Updates.set("type", it)) }
        request.description?.let { updates.add(Updates.set("description", it)) }
        request.pricePerMonth?.let { updates.add(Updates.set("pricePerMonth", it)) }
        request.location?.let { updates.add(Updates.set("location", it)) }
        request.isAvailable?.let { updates.add(Updates.set("isAvailable", it)) }
        request.sizeInSquareMeters?.let { updates.add(Updates.set("sizeInSquareMeters", it)) }
        request.images?.let { updates.add(Updates.set("images", it)) }
        request.amenities?.let { updates.add(Updates.set("amenities", it)) }
        request.leaseTerms?.let { updates.add(Updates.set("leaseTerms", it)) }
        request.rating?.let { updates.add(Updates.set("rating", it)) }

        // if no updates are provided, return the original/existing property
        if (updates.isEmpty()) return findPropertyById(id)

        // Always update the updatedAt field timestamp
        updates.add(Updates.set("updatedAt", Clock.System.now().toLocalDateTime(TimeZone.UTC)))

        collection.updateOne(eq("_id", ObjectId(id)), Updates.combine(updates))
        return findPropertyById(id)
    }

    /**
     * Delete a property by ID
     */
    override suspend fun deleteProperty(id: String): Boolean {
        if (!ObjectId.isValid(id)) return false
        val result = collection.deleteOne(eq("_id", ObjectId(id)))
        return result.deletedCount > 0
    }

    override suspend fun findPropertyByOwnerId(ownerId: String): List<Property> {
        return collection.find(eq("propertyOwnerId", ownerId)).toList()
    }

    override suspend fun updatePropertyAvailability(
        id: String,
        isAvailable: Boolean
    ): Boolean {
        if (!ObjectId.isValid(id)) return false
        val result = collection.updateOne(
            eq("_id", ObjectId(id)),
            Updates.combine(
                Updates.set("isAvailable", isAvailable),
                Updates.set("updatedAt", Clock.System.now().toLocalDateTime(TimeZone.UTC))
            )
        )
        return result.modifiedCount > 0
    }

    override suspend fun searchProperties(query: String): List<Property> {
        val searchFilter = or(
            regex("name", ".*$query.*", "i"),
            regex("description", ".*$query.*", "i"),
            regex("location", ".*$query.*", "i"),
            regex("type", ".*$query.*", "i")
        )
        return collection.find(searchFilter).toList()
    }
}