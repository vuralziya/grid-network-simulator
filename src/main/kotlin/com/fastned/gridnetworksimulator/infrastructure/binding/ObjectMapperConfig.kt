package com.fastned.gridnetworksimulator.infrastructure.binding

import com.couchbase.client.java.json.JsonValueModule
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import java.time.Instant

@Configuration
class ObjectMapperConfig {

    companion object {
        fun createObjectMapperWithDefaults(): ObjectMapper {
            val isoInstantModule = SimpleModule()
                .addSerializer(Instant::class.java, IsoInstantSerializer())
                .addDeserializer(Instant::class.java, IsoInstantDeserializer())

            return ObjectMapper()
                .registerKotlinModule()
                .registerModule(JavaTimeModule())
                .registerModule(isoInstantModule)
            //.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        }

        fun createObjectMapperForCouchbase(): ObjectMapper {
            return createObjectMapperWithDefaults()
                .registerModule(JsonValueModule())
        }
    }

    @Primary
    @Bean
    fun objectMapper(): ObjectMapper {
        return createObjectMapperWithDefaults()
    }
}
