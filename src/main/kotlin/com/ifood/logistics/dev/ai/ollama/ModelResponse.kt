package com.ifood.logistics.dev.ai.ollama

import com.fasterxml.jackson.annotation.JsonInclude
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

@JsonInclude(JsonInclude.Include.NON_NULL)
data class AssistantResponseDto (
    val model: String,
    @field:JsonProperty("created_at")
    val createdAt: Instant,
    val message: ModelResponseDto,
    val done: Boolean,
    @field:JsonProperty("done_reason")
    val doneReason: String? = null,
    @field:JsonProperty("total_duration")
    val totalDuration: Long? = null,
    @field:JsonProperty("load_duration")
    val loadDuration: Long? = null,
    @field:JsonProperty("prompt_eval_count")
    val promptEvalCount: Int? = null,
    @field:JsonProperty("prompt_eval_duration")
    val promptEvalDuration: Long? = null,
    @field:JsonProperty("eval_count")
    val evalCount: Int? = null,
    @field:JsonProperty("eval_duration")
    val evalDuration: Long? = null,
)

data class ModelResponseDto(
    val role: String,
    val content: String
)

// create a static factory class for the StreamMessageDto
object StreamMessageFactory {

    fun createPartialResponse(generateRequestDto: GenerateRequestDto,
                              partialContent: String): AssistantResponseDto {
        return AssistantResponseDto(
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
                              chatResponse: ChatResponse): AssistantResponseDto {

        return AssistantResponseDto(
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