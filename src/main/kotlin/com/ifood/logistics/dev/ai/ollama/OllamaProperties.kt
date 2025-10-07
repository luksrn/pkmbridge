package com.ifood.logistics.dev.ai.ollama

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "ollama")
data class OllamaProperties(
    var modelName: String = "",
    var baseUrl: String = "",
    var timeout: Long = 0,
    var temperature: Double = 0.0,
    var topP: Double = 0.0,
    var topK: Int = 0,
    var logRequests: Boolean = false,
    var logResponses: Boolean = false,
)
