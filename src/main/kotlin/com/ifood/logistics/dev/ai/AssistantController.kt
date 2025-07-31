package com.ifood.logistics.dev.ai

import com.ifood.logistics.dev.ai.pkm.Assistant
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class AssistantController(val assistant: Assistant) {

    @GetMapping("/chat")
    @ResponseBody
    fun model(@RequestParam(value = "message", defaultValue = "Hello") message: String): String {
        var result = assistant.chat(message)
        return result.content() //+ "\n\nSources = " + result.sources()
    }
}