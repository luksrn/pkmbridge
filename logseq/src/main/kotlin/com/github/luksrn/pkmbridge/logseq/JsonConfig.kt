package com.github.luksrn.pkmbridge.logseq

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import tools.jackson.databind.DeserializationFeature
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.KotlinFeature
import tools.jackson.module.kotlin.KotlinModule

@Configuration
class JsonConfig {
    @Bean
    fun objectMapper(): JsonMapper {
        val kotlinModule =
            KotlinModule
                .Builder()
                .enable(KotlinFeature.StrictNullChecks)
                .build()

        val mapper =
            JsonMapper
                .builder()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .addModule(kotlinModule)
                .build()

        return mapper
    }
}