package com.ifood.logistics.dev.ai.pkm

import dev.langchain4j.agent.tool.P
import dev.langchain4j.agent.tool.Tool

class LogseqApiTool {

    @Tool("Returns the feedbacks of a given person")
    fun getWeather(
        @P("The person name") username: String,
    ): String {
        return """
            {
                "date": "12-10-2023",
                "feedback": "This is a sample feedback for the ${username}.",
            }
        """
    }
}