package com.github.luksrn.pkmbridge

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import dev.langchain4j.model.chat.response.ChatResponse
import java.time.Instant

@JsonInclude(JsonInclude.Include.NON_NULL)
data class AssistantResponseDto(
    val model: String,
    @field:JsonProperty("created_at")
    val createdAt: Instant,
    val message: ModelResponseDto,
    val done: Boolean,
    @field:JsonProperty("response")
    val response: String? = null,
    @field:JsonProperty("done_reason")
    val doneReason: String? = null,
    // total_duration: How long the response took to generate
    @field:JsonProperty("total_duration")
    var totalDuration: Long? = null,
    // load_duration: How long the model took to load
    @field:JsonProperty("load_duration")
    val loadDuration: Long? = null,
    // prompt_eval_count: How many input tokens were processed
    @field:JsonProperty("prompt_eval_count")
    val promptEvalCount: Int? = null,
    // prompt_eval_duration: How long it took to evaluate the prompt
    @field:JsonProperty("prompt_eval_duration")
    val promptEvalDuration: Long? = null,
    // eval_count: How many output tokens were processes
    @field:JsonProperty("eval_count")
    val evalCount: Int? = null,
    // eval_duration: How long it took to generate the output tokens
    @field:JsonProperty("eval_duration")
    var evalDuration: Long? = null,
)

data class ModelResponseDto(
    val role: String,
    val content: String,
)

data class ChatContext(
    val generateRequestDto: GenerateRequestDto,
    val assistantResponseDto: AssistantResponseDto,
    val start: Instant = Instant.now(),
)

// create a static factory class for the StreamMessageDto
object StreamMessageFactory {
    fun createPartialResponse(
        generateRequestDto: GenerateRequestDto,
        partialContent: String,
    ): AssistantResponseDto =
        AssistantResponseDto(
            model = generateRequestDto.model,
            createdAt = Instant.now(),
            message =
                ModelResponseDto(
                    role = "assistant",
                    content = partialContent,
                ),
            done = false,
        )

    fun createCompleteResponse(
        generateRequestDto: GenerateRequestDto,
        chatResponse: ChatResponse,
    ): AssistantResponseDto =
        AssistantResponseDto(
            model = chatResponse.modelName(),
            createdAt = Instant.now(),
            message =
                ModelResponseDto(
                    role = "assistant",
                    content = "",
                ),
            response = if (generateRequestDto.stream) null else chatResponse.aiMessage().text(),
            done = true,
            doneReason = chatResponse.finishReason().name,
            totalDuration = 0L,
            loadDuration = 0L,
            promptEvalCount = chatResponse.tokenUsage().inputTokenCount(),
            promptEvalDuration = 0L,
            evalCount = chatResponse.tokenUsage().totalTokenCount(),
            evalDuration = 0L,
        )
}
