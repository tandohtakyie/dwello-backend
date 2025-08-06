package utils

import kotlinx.datetime.LocalDateTime
import org.bson.BsonReader
import org.bson.BsonType
import org.bson.BsonWriter
import org.bson.codecs.Codec
import org.bson.codecs.DecoderContext
import org.bson.codecs.EncoderContext

class MongoLocalDateTimeCodec: Codec<LocalDateTime> {
    override fun encode(
        writer: BsonWriter?,
        value: LocalDateTime?,
        encoderContext: EncoderContext?
    ) {
        if (value != null) {
            writer?.writeString(value.toString())
        } else {
            writer?.writeNull()
        }
    }

    override fun getEncoderClass(): Class<LocalDateTime?>? {
        @Suppress("UNCHECKED_CAST")
        return LocalDateTime::class.java as Class<LocalDateTime?>?
    }

    override fun decode(
        reader: BsonReader?,
        decoderContext: DecoderContext?
    ): LocalDateTime? {
        // Check if the BSON value is null before trying to read it as a string
        if (reader?.currentBsonType == BsonType.NULL) {
            reader.readNull() // Consume the null value
            return null
        }
        val s = reader?.readString() // Read the string if it's not null
        return LocalDateTime.parse(s.toString())
    }
}