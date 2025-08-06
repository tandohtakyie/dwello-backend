package config

import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase

interface DatabaseConfig {
    val client: MongoClient
    val database: MongoDatabase
    fun close()
}