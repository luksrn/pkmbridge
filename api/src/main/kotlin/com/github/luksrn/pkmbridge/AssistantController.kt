package com.github.luksrn.pkmbridge

import com.github.luksrn.pkmbridge.StreamMessageFactory.createCompleteResponse
import com.github.luksrn.pkmbridge.StreamMessageFactory.createGuardrailResponse
import com.github.luksrn.pkmbridge.StreamMessageFactory.createPartialResponse
import dev.langchain4j.guardrail.InputGuardrailException
import dev.langchain4j.guardrail.OutputGuardrailException
import dev.langchain4j.model.ollama.OllamaModels
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks
import java.util.UUID

@RestController
@CrossOrigin(origins = ["*"])
class AssistantController(
    val ollamaModel: OllamaModels,
    val personalKnowledgeAssistant: PersonalKnowledgeAssistant,
) {
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
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_NDJSON_VALUE],
    )
    fun generate(
        @RequestBody generateRequest: GenerateRequestDto,
    ): Flux<AssistantResponseDto> = chatStream(generateRequest)

    private fun chatStream(generateRequest: GenerateRequestDto): Flux<AssistantResponseDto> {
        val sink = Sinks.many().unicast().onBackpressureBuffer<AssistantResponseDto>()
        val start = System.nanoTime()
        try {
            personalKnowledgeAssistant
                .chatStream(UUID.randomUUID().toString(), generateRequest.prompt)
                .onPartialResponse { partialResponse ->
                    if (generateRequest.stream) {
                        val chatResponse = createPartialResponse(generateRequest, partialResponse)
                        sink.tryEmitNext(chatResponse)
                    }
                }.onError { e ->
                    if (e is OutputGuardrailException) {
                        logger.warn(e.message)
                        sink.tryEmitNext(createGuardrailResponse(generateRequest, e))
                    }
                    sink.tryEmitComplete()
                }.onCompleteResponse {
                    val chatResponse = createCompleteResponse(generateRequest, it)
                    chatResponse.totalDuration = System.nanoTime() - start
                    chatResponse.evalDuration = chatResponse.totalDuration
                    sink.tryEmitNext(chatResponse)
                    sink.tryEmitComplete()
                }.start()

            return sink.asFlux()
        } catch (e: InputGuardrailException) {
            logger.warn(e.message)
            sink.tryEmitNext(createGuardrailResponse(generateRequest, e))
            sink.tryEmitComplete()

            return sink.asFlux()
        }
    }

    @GetMapping("/api/version")
    fun version(): Map<String, String> = mapOf("version" to "0.5.1")

    @GetMapping("/api/tags")
    fun availableModels() = mapOf("models" to ollamaModel.availableModels().content())

    @GetMapping("/api/ps")
    fun runningModels() = mapOf("models" to ollamaModel.runningModels().content())

    private val logger = LoggerFactory.getLogger(AssistantController::class.java)
}
