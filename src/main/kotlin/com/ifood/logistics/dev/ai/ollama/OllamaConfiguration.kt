package com.ifood.logistics.dev.ai.ollama

import dev.langchain4j.model.chat.ChatModel
import dev.langchain4j.model.chat.StreamingChatModel
import dev.langchain4j.model.ollama.OllamaChatModel
import dev.langchain4j.model.ollama.OllamaModels
import dev.langchain4j.model.ollama.OllamaStreamingChatModel
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
class OllamaConfiguration(
    val ollamaProperties: OllamaProperties,
) {

    @Bean
    fun streamChatModel() : StreamingChatModel =
        OllamaStreamingChatModel
            .builder()
            .baseUrl(ollamaProperties.baseUrl)
            .temperature(ollamaProperties.temperature) // temperature (between 0 and 2)
            .topP(ollamaProperties.topP) // topP (between 0 and 1) — cumulative probability of the most probable tokens
            .topK(ollamaProperties.topK)
            .logRequests(ollamaProperties.logRequests)
            .logResponses(ollamaProperties.logResponses)
            .modelName(ollamaProperties.modelName)
            .timeout(Duration.ofSeconds(ollamaProperties.timeout))
            .build()

    @Bean
    fun ollamaModels(): OllamaModels =
        OllamaModels
            .builder()
            .baseUrl(ollamaProperties.baseUrl)
            .build()

    @Bean
    fun chatModel() : ChatModel =
        OllamaChatModel
            .builder()
            .baseUrl(ollamaProperties.baseUrl)
            .temperature(ollamaProperties.temperature) // temperature (between 0 and 2)
            .topP(ollamaProperties.topP) // topP (between 0 and 1) — cumulative probability of the most probable tokens
            .topK(ollamaProperties.topK)
            .logRequests(ollamaProperties.logRequests)
            .logResponses(ollamaProperties.logResponses)
            .modelName(ollamaProperties.modelName)
            .timeout(Duration.ofSeconds(ollamaProperties.timeout))
            .build()
}