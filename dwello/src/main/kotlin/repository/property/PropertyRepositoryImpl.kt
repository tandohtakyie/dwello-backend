package repository.property

import com.mongodb.client.model.Filters.all
import com.mongodb.client.model.Filters.and
import com.mongodb.client.model.Filters.empty
import com.mongodb.client.model.Filters.eq
import com.mongodb.client.model.Filters.gte
import com.mongodb.client.model.Filters.`in`
import com.mongodb.client.model.Filters.lte
import com.mongodb.client.model.Filters.or
import com.mongodb.client.model.Filters.regex
import com.mongodb.client.model.FindOneAndUpdateOptions
import com.mongodb.client.model.ReturnDocument
import com.mongodb.client.model.Updates
import com.mongodb.kotlin.client.coroutine.MongoCollection
import config.DatabaseConfig
import dto.property.PropertyFilter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import model.property.Property
import org.bson.conversions.Bson
import org.bson.types.ObjectId
import utils.Constants.Collections.PROPERTIES

class PropertyRepositoryImpl(
    databaseConfig: DatabaseConfig
) : PropertyRepository {

    private val propertiesCollection: MongoCollection<Property> =
        databaseConfig.database.getCollection(collectionName = PROPERTIES)

    override suspend fun createProperty(property: Property): Property? {
        val result = propertiesCollection.insertOne(property)
        return if (result.wasAcknowledged()) property else null
    }

    override suspend fun findPropertyById(id: String): Property? {
        // TODO check if condition
        if (!ObjectId.isValid(id)) return null
        return propertiesCollection.find(eq("_id", ObjectId(id))).firstOrNull()
    }

    override suspend fun findAllProperties(
        filter: PropertyFilter?,
        page: Int,
        pageSize: Int
    ): Pair<List<Property>, Long> {
        val queryFilters = mutableListOf<Bson>()
        filter?.let {
            it.type?.let { type -> queryFilters.add(eq("type", type)) }
            it.listingType?.let { lt ->
                queryFilters.add(
                    eq(
                        "listingType",
                        lt.name
                    )
                )
            } // Store enum as string
            it.location?.let { loc ->
                queryFilters.add(
                    regex(
                        "location",
                        ".*$loc.*",
                        "i"
                    )
                )
            } // Case-insensitive partial match
            it.minPrice?.let { minP -> queryFilters.add(gte("price", minP)) }
            it.maxPrice?.let { maxP -> queryFilters.add(lte("price", maxP)) }
            it.minSize?.let { minS -> queryFilters.add(gte("sizeInSquareMeters", minS)) }
            it.maxSize?.let { maxS -> queryFilters.add(lte("sizeInSquareMeters", maxS)) }
            it.isAvailable?.let { avail -> queryFilters.add(eq("isAvailable", avail)) }
            it.propertyOwnerId?.let { ownerId -> queryFilters.add(eq("propertyOwnerId", ownerId)) }
            it.amenities?.let { amenitiesList ->
                if (amenitiesList.isNotEmpty()) {
                    // Ensures all specified amenities are present in the property's amenities list
                    queryFilters.add(all("amenities", amenitiesList))
                    // If you want to match properties that have AT LEAST ONE of the specified amenities:
                    // queryFilters.add(Filters.`in`("amenities", amenitiesList))
                }
            }
        }

        val combinedFilter = if (queryFilters.isNotEmpty()) and(queryFilters) else empty()

        val total = propertiesCollection.countDocuments(combinedFilter)

        val skip = (page - 1) * pageSize
        val properties = propertiesCollection
            .find(combinedFilter)
            .skip(skip)
            .limit(pageSize)
//            .sort(Sorts.descending("createdAt")) //TODO check the sorting as default sorting
            .toList()

        return Pair(properties, total)
    }

    override suspend fun updateProperty(
        id: String,
        updates: Map<String, Any>
    ): Property? {
        if (!ObjectId.isValid(id)) return null

        val bsonUpdates = mutableListOf<Bson>()
        updates.forEach { (key, value) ->
            // Skip trying to update 'id' or '_id'
            if (key != "id" && key != "_id") {
                // TODO check this condition
                if (value != null) {
                    // For enums like ListingType, ensure they are stored correctly (e.g., as String name)
                    val updateValue = if (value is Enum<*>) value.name else value
                    bsonUpdates.add(Updates.set(key, updateValue))
                } else {
                    bsonUpdates.add(Updates.unset(key)) // If value is explicitly null, unset the field
                }
            }
        }

        if (bsonUpdates.isEmpty()) {
            return findPropertyById(id)
        }

        bsonUpdates.add(Updates.set("updatedAt", Clock.System.now().toLocalDateTime(TimeZone.UTC)))
        val updateOptions = FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER)

        return propertiesCollection.findOneAndUpdate(
            eq("_id", ObjectId(id)),
            Updates.combine(bsonUpdates),
            updateOptions
        )
    }

    override suspend fun deleteProperty(id: String): Boolean {
        if (!ObjectId.isValid(id)) return false
        val result = propertiesCollection.deleteOne(eq("_id", ObjectId(id)))
        return result.deletedCount > 0
    }

    override suspend fun findPropertiesByOwnerId(ownerId: String): List<Property> {
        return propertiesCollection.find(eq("propertyOwnerId", ownerId)).toList()
    }

    override suspend fun findPropertiesByIds(ids: List<String>): List<Property> {
        if (ids.isEmpty()) return emptyList()
        val objectIds = ids.mapNotNull { if (ObjectId.isValid(it)) ObjectId(it) else null }
        if (objectIds.isEmpty() && ids.isNotEmpty()) return emptyList()
        return propertiesCollection.find(`in`("_id", objectIds)).toList()
    }

    override suspend fun updatePropertyAvailability(
        propertyId: String,
        isAvailable: Boolean
    ): Boolean {
        if (!ObjectId.isValid(propertyId)) return false
        val result = propertiesCollection.updateOne(
            eq("_id", ObjectId(propertyId)),
            Updates.combine(
                Updates.set("isAvailable", isAvailable),
                Updates.set("updatedAt", Clock.System.now().toLocalDateTime(TimeZone.UTC))
            )
        )
        return result.modifiedCount > 0
    }

    override suspend fun searchProperties(
        query: String,
        page: Int,
        pageSize: Int
    ): Pair<List<Property>, Long> {
        val searchPattern = ".*$query.*" // Basic substring search
        val searchFilter = or(
            regex("name", searchPattern, "i"), // "i" for case-insensitive
            regex("description", searchPattern, "i"),
            regex("location", searchPattern, "i"),
            regex("type", searchPattern, "i")
            //TODO Potentially add more fields to search
        )

        val total = propertiesCollection.countDocuments(searchFilter)
        val properties = propertiesCollection.find(searchFilter)
            .skip((page - 1) * pageSize)
            .limit(pageSize)
            .toList()

        return Pair(properties, total)
    }

}