package com.github.luksrn.pkmbridge.logseqdb

import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order

@Configuration
class LogseqRAGInitializer {
    @Deprecated("Use initializer instead")
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    fun logseqAvailabilityCheck(logRestClient: LogseqRestClient) =
        ApplicationRunner { args ->
            try {
                logRestClient.getCurrentGraph()
            } catch (ex: java.lang.Exception) {
                throw LogseqAvailabilityCheckException(ex)
            }
        }
}
