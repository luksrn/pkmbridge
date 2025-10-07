package com.ifood.logistics.dev.ai

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.ifood.logistics.dev.ai.ollama.StreamMessageFactory
import com.ifood.logistics.dev.ai.pkm.Assistant
import dev.langchain4j.model.ollama.OllamaModels
import kotlinx.serialization.Serializable
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks
import java.time.Instant
import java.util.UUID


@RestController
@CrossOrigin(origins = ["*"])
class OpenApiProxy(
    val ollamaModel: OllamaModels,
    val assistant: Assistant){

    val objectMapper = ObjectMapper()
        .registerModule(JavaTimeModule())
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)

    @GetMapping("/api/version")
fun version(): Map<String, String> {
        return mapOf("version" to "0.5.1")
    }
    @GetMapping("/api/tags")
    fun models() = mapOf("models" to ollamaModel.availableModels().content())

    @GetMapping("/api/ps")
    fun runningModels() = mapOf("models" to ollamaModel.runningModels().content())

    @PostMapping("/api/chat",
        consumes = ["application/json"],
        produces = ["application/x-ndjson"])
    fun stream(@RequestBody chatMessage: ChatRequestDto):  Flux<String> {
        return streamResponseTo(GenerateRequestDto(
            model = chatMessage.model,
            prompt = chatMessage.messages.last().content,
            stream = chatMessage.stream,
            format = chatMessage.format
        ))
    }

    @PostMapping("/api/generate",
        consumes = ["application/json"],
        produces = ["application/json","application/x-ndjson"])
    fun generate(@RequestBody generateRequest: GenerateRequestDto) : Flux<String>{
        return streamResponseTo(generateRequest)
    }

    private fun streamResponseTo(generateRequest: GenerateRequestDto) : Flux<String>{
        val sink = Sinks.many().unicast().onBackpressureBuffer<String>()
        assistant.chatStream(UUID.randomUUID().toString(), generateRequest.prompt)
            .onPartialResponse { partialResponse ->
                if(generateRequest.stream) {
                    val chatResponse = StreamMessageFactory.createPartialResponse(generateRequest, partialResponse)
                    sink.tryEmitNext(objectMapper.writeValueAsString(chatResponse) + "\n")
                }
            }
            .onError {
                if(generateRequest.stream)
                    sink.tryEmitNext("""{"model":"gemma3","created_at":"${Instant.now()}","message":{"role":"assistant","content":""},"done_reason":"${it.message}","done":true,"total_duration":17786754667,"load_duration":94432792,"prompt_eval_count":15,"prompt_eval_duration":1099568333,"eval_count":654,"eval_duration":16592188334}""")
                sink.tryEmitComplete()
            }
            .onCompleteResponse {
                if(generateRequest.stream) {
                    val chatResponse = StreamMessageFactory.createCompleteResponse(generateRequest, it)
                    sink.tryEmitNext(objectMapper.writeValueAsString(chatResponse) + "\n")
                } else {
                    // TODO this is the final response, not a stream, to "generate" endpoint
                    val response = it.aiMessage().text().replace("\n","\\n").replace("\"", "\\\"")
                    sink.tryEmitNext("""{"model":"${it.modelName()}","created_at":"${Instant.now()}","response": "${response},"done_reason":"${it.finishReason().name}","done":true,"total_duration":17786754667,"load_duration":94432792,"prompt_eval_count":15,"prompt_eval_duration":1099568333,"eval_count":654,"eval_duration":16592188334}""")
                }

                sink.tryEmitComplete()
            }
            .onToolExecuted {
                System.out.println(it)
            }
            .start()
        return sink.asFlux();
    }
}

@Serializable
data class  GenerateRequestDto(
    val model: String,
    val prompt: String,
    val stream: Boolean = false,
    val format: String? = null
)

@Serializable
data class Options(
    val temperature: Double,
    val top_k: Int,
    val top_p: Double,
    val stop: List<String> = emptyList()
)

@Serializable
data class ChatRequestDto(
    val model: String,
    val messages: List<Message>,
    val options: Options? = null,
    val stream: Boolean = false,
    //val tools: List<String> = emptyList()
    val format: String? = null
)

@Serializable
data class Message(
    val role: String,
    val content: String,
)
// https://ollama.com/blog/structured-outputs

