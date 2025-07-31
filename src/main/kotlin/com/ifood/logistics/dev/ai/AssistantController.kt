package com.ifood.logistics.dev.ai

import com.ifood.logistics.dev.ai.pkm.Assistant
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import reactor.core.publisher.Flux
import reactor.core.publisher.FluxSink
import reactor.core.publisher.Sinks


@Controller
class AssistantController(val assistant: Assistant) {

    @GetMapping("/chat")
    @ResponseBody
    fun model(@RequestParam(value = "message", defaultValue = "Hello") message: String): String {
        var result = assistant.chat(message)
        return result.content() //+ "\n\nSources = " + result.sources()
    }

    @GetMapping("/chat-stream")
    @ResponseBody
    fun stream(@RequestParam(value = "message", defaultValue = "Hello") message: String): Flux<String> {
        val sink = Sinks.many().unicast().onBackpressureBuffer<String>()
        assistant.chatStream(message)
            .onRetrieved {
//                it.forEach { segment ->
//                    sink.tryEmitNext(segment.metadata().toString())
//                }
            }
            .onPartialResponse { sink.tryEmitNext(it) }
            .onError { sink.tryEmitNext(it.message?: "Error") }
            .onCompleteResponse {
                //sink.tryEmitNext(it.aiMessage().text())
                sink.tryEmitComplete()
            }
            .start()

        return sink.asFlux();
    }

}