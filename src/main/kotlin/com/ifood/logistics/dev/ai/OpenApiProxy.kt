package com.ifood.logistics.dev.ai

import com.ifood.logistics.dev.ai.pkm.Assistant
import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.http.client.sse.ServerSentEvent
import dev.langchain4j.model.chat.request.ChatRequest
import dev.langchain4j.model.chat.response.ChatResponse
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler
import dev.langchain4j.model.ollama.OllamaModels
import dev.langchain4j.model.ollama.OllamaStreamingChatModel
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks
import java.time.Instant


@RestController
@CrossOrigin(origins = ["*"])
class OpenApiProxy(val ollamaModel: OllamaModels,
    val model: OllamaStreamingChatModel,
    val assistant: Assistant){

    @GetMapping("/api/version")
    fun version() = mapOf( "version" to "0.5.1")

    @GetMapping("/api/tags")
    fun models() = mapOf("models" to ollamaModel.availableModels().content())


    @PostMapping("/api/chat",
        consumes = ["application/json", "application/x-www-form-urlencoded"],
        produces = ["application/x-ndjson"])
    fun stream(@RequestBody s:String):  Flux<String> {

        val chatMessage = Json{ignoreUnknownKeys = true}.decodeFromString<ChatRequestDto>(s)


        val sink = Sinks.many().unicast().onBackpressureBuffer<String>()
        assistant.chatStream(chatMessage.messages.last().content)
            .onPartialResponse { partialResponse ->
                sink.tryEmitNext("{\"model\":\"gemma3\",\"created_at\":\"${Instant.now()}\",\"message\":{\"role\":\"assistant\",\"content\":\"${partialResponse.replace("\n","\\n")}\"},\"done\":false}\n")
            }
            .onError {

            }
            .onCompleteResponse {
                sink.tryEmitNext("""{"model":"gemma3","created_at":"2025-08-03T02:42:16.060952Z","message":{"role":"assistant","content":""},"done_reason":"stop","done":true,"total_duration":17786754667,"load_duration":94432792,"prompt_eval_count":15,"prompt_eval_duration":1099568333,"eval_count":654,"eval_duration":16592188334}""")
                sink.tryEmitComplete()
            }.start()

//
//        model.chat(userMessage, object : StreamingChatResponseHandler {
//            override fun onPartialResponse(partialResponse: String) {
//                //sink.tryEmitNext(partialResponse)
//                sink.tryEmitNext("{\"model\":\"gemma3\",\"created_at\":\"${Instant.now()}\",\"message\":{\"role\":\"assistant\",\"content\":\"${partialResponse.replace("\n","\\n")}\"},\"done\":false}\n")
//            }
//
//            override fun onCompleteResponse(completeResponse: ChatResponse) {
//                //sink.tryEmitNext(completeResponse.aiMessage().text()!!)
////                futureResponse.complete(completeResponse)
//                sink.tryEmitNext("""{"model":"gemma3","created_at":"2025-08-03T02:42:16.060952Z","message":{"role":"assistant","content":""},"done_reason":"stop","done":true,"total_duration":17786754667,"load_duration":94432792,"prompt_eval_count":15,"prompt_eval_duration":1099568333,"eval_count":654,"eval_duration":16592188334}""")
//                sink.tryEmitComplete()
//            }
//
//            override fun onError(error: Throwable?) {
//  //              futureResponse.completeExceptionally(error)
//                sink.tryEmitComplete()
//            }
//        })

    //    futureResponse.join()

        return sink.asFlux();
    }

    @PostMapping("/api/generate")
    fun chatCompletations() = mapOf("models" to listOf(ollamaModel.availableModels().content().first()))
}

@Serializable
data class ChatRequestDto(
    val model: String,
    val messages: List<Message>
)

@Serializable
data class Message(
    val role: String,
    val content: String
)