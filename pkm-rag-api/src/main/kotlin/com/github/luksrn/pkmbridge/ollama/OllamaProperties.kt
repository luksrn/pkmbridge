package com.github.luksrn.pkmbridge.ollama

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.Positive
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated

@Validated
@Component
@ConfigurationProperties(prefix = "ollama")
data class OllamaProperties(
    @field:NotEmpty
    var modelName: String = "",
    @field:NotEmpty
    var baseUrl: String = "",
    @field:Positive
    var timeout: Long = 0,
    @field:Positive
    var temperature: Double = 0.0,
    @field:Positive
    var topP: Double = 0.0,
    @field:Positive
    var topK: Int = 0,
    var logRequests: Boolean = false,
    var logResponses: Boolean = false,
)
