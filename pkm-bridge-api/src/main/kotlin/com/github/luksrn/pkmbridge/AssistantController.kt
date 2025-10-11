package com.github.luksrn.pkmbridge

import com.github.luksrn.pkmbridge.ollama.AssistantResponseDto
import com.github.luksrn.pkmbridge.ollama.ChatRequestDto
import com.github.luksrn.pkmbridge.ollama.GenerateRequestDto
import com.github.luksrn.pkmbridge.ollama.StreamMessageFactory
import dev.langchain4j.model.ollama.OllamaModels
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks
import java.util.UUID

@RestController
@CrossOrigin(origins = ["*"])
class AssistantController(
    val ollamaModel: OllamaModels,
    val assistant: Assistant,
) {
    @GetMapping("/api/version")
    fun version(): Map<String, String> = mapOf("version" to "0.5.1")

    @GetMapping("/api/tags")
    fun availableModels() = mapOf("models" to ollamaModel.availableModels().content())

    @GetMapping("/api/ps")
    fun runningModels() = mapOf("models" to ollamaModel.runningModels().content())

    @PostMapping(
        "/api/chat",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_NDJSON_VALUE],
    )
    fun chat(
        @RequestBody chatMessage: ChatRequestDto,
    ): Flux<AssistantResponseDto> =
        chatStream(
            GenerateRequestDto(
                model = chatMessage.model,
                prompt = chatMessage.messages.last().content,
                stream = chatMessage.stream,
                format = chatMessage.format,
            ),
        )

    @PostMapping(
        "/api/generate",
        consumes = ["application/json"],
        produces = ["application/json", "application/x-ndjson"],
    )
    fun generate(
        @RequestBody generateRequest: GenerateRequestDto,
    ): Flux<AssistantResponseDto> = chatStream(generateRequest)

    private fun chatStream(generateRequest: GenerateRequestDto): Flux<AssistantResponseDto> {
        val sink = Sinks.many().unicast().onBackpressureBuffer<AssistantResponseDto>()
        assistant
            .chatStream(UUID.randomUUID().toString(), generateRequest.prompt)
            .onPartialResponse { partialResponse ->
                if (generateRequest.stream) {
                    val chatResponse = StreamMessageFactory.createPartialResponse(generateRequest, partialResponse)
                    sink.tryEmitNext(chatResponse)
                }
            }.onError {
                if (generateRequest.stream) {
                    // sink.tryEmitNext("""{"model":"gemma3","created_at":"${Instant.now()}","message":{"role":"assistant","content":""},"done_reason":"${it.message}","done":true,"total_duration":17786754667,"load_duration":94432792,"prompt_eval_count":15,"prompt_eval_duration":1099568333,"eval_count":654,"eval_duration":16592188334}""")
                }
                sink.tryEmitComplete()
            }.onCompleteResponse {
                val chatResponse = StreamMessageFactory.createCompleteResponse(generateRequest, it)
                sink.tryEmitNext(chatResponse)
                sink.tryEmitComplete()
            }.onToolExecuted {
                // println(it)
            }.start()
        return sink.asFlux()
    }
}
