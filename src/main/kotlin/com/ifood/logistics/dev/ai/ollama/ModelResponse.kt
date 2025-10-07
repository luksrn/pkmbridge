package com.ifood.logistics.dev.ai.ollama

import com.fasterxml.jackson.annotation.JsonProperty
import com.ifood.logistics.dev.ai.GenerateRequestDto
import dev.langchain4j.model.chat.response.ChatResponse
import java.time.Instant

data class StreamMessageDto(
    val model: String,
    @field:JsonProperty("created_at")
    val createdAt: Instant,
    val message: ModelResponseDto,
    val done: Boolean
)

data class CompleteStreamMessageDto (
    val model: String,
    @field:JsonProperty("created_at")
    val createdAt: Instant,
    val message: ModelResponseDto,
    @field:JsonProperty("done_reason")
    val doneReason: String,
    val done: Boolean,
    @field:JsonProperty("total_duration")
    val totalDuration: Long,
    @field:JsonProperty("load_duration")
    val loadDuration: Long,
    @field:JsonProperty("prompt_eval_count")
    val promptEvalCount: Int,
    @field:JsonProperty("prompt_eval_duration")
    val promptEvalDuration: Long,
    @field:JsonProperty("eval_count")
    val evalCount: Int,
    @field:JsonProperty("eval_duration")
    val evalDuration: Long
)

data class ModelResponseDto(
    val role: String,
    val content: String
)

// create a static factory class for the StreamMessageDto
object StreamMessageFactory {

    fun createPartialResponse(generateRequestDto: GenerateRequestDto,
                              partialContent: String): StreamMessageDto {
        return StreamMessageDto(
            model = generateRequestDto.model,
            createdAt = Instant.now(),
            message = ModelResponseDto(
                role = "assistant",
                content = partialContent
            ),
            done = false
        )
    }

    fun createCompleteResponse(generateRequestDto: GenerateRequestDto,
                              chatResponse: ChatResponse): CompleteStreamMessageDto {

        return CompleteStreamMessageDto(
            model = generateRequestDto.model,
            createdAt = Instant.now(),
            message = ModelResponseDto(
                role = "assistant",
                content = chatResponse.aiMessage().text()
            ),
            done = true,
            doneReason = chatResponse.finishReason().name,
            totalDuration = 0L,
            loadDuration = 0L,
            promptEvalCount = 0,
            promptEvalDuration = 0L,
            evalCount = 0,
            evalDuration = 0L
        )
    }
}