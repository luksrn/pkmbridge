package com.ifood.logistics.dev.ai.pkm

import dev.langchain4j.service.Result
import dev.langchain4j.service.SystemMessage

interface SummarizerAssistant {

    @SystemMessage("""
You are an assistant tasked with summarizing tables and text. \
Give a concise summary of the table or text. Table or text chunk:
""")
    fun summarize(userMessage: String) : Result<String>

}