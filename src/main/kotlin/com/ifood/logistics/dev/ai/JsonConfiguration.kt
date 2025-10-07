package com.ifood.logistics.dev.ai

import com.fasterxml.jackson.databind.SerializationFeature
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JsonConfiguration {

    @Bean
    fun objectMapper() = com.fasterxml.jackson.databind.ObjectMapper()
        .registerModule(com.fasterxml.jackson.datatype.jsr310.JavaTimeModule())
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
}