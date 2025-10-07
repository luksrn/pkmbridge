package com.ifood.logistics.dev.ai

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import kotlinx.serialization.json.Json
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.KotlinSerializationJsonHttpMessageConverter

@Configuration
class JsonConfiguration {
    @Bean
    fun objectMapper() =
        ObjectMapper()
            .registerModule(
                JavaTimeModule(),
            ).configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)

    // TODO: use only one Json configuration
    @Bean
    fun ktxMessageConverter(): KotlinSerializationJsonHttpMessageConverter {
        // if you want to ignore unknown keys from json string,
        // otherwise make sure your data class has all json keys.
        val json = Json { ignoreUnknownKeys = true }
        return KotlinSerializationJsonHttpMessageConverter(json)
    }
}
