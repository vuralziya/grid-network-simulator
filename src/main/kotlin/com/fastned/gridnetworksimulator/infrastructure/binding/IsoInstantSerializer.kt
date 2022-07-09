package com.fastned.gridnetworksimulator.infrastructure.binding

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import java.io.IOException
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAccessor

class IsoInstantDeserializer : JsonDeserializer<Instant>() {
    override fun deserialize(parser: JsonParser, context: DeserializationContext): Instant {
        val string: String = parser.text.trim()
        return try {
            (DateTimeFormatter.ISO_INSTANT.parse(string) { temporal: TemporalAccessor? -> Instant.from(temporal) } as Instant)
        } catch (ex: Exception) {
            Instant.ofEpochSecond(string.toLong())
        }
    }
}

class IsoInstantSerializer : JsonSerializer<Instant>() {
    @Throws(IOException::class, JsonProcessingException::class)
    override fun serialize(value: Instant, gen: JsonGenerator, serializers: SerializerProvider?) {
        gen.writeString(value.toString())
    }
}
