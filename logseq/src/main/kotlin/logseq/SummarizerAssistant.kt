package com.ifood.logistics.dev.ai.logseq

import dev.langchain4j.service.Result
import dev.langchain4j.service.SystemMessage

interface SummarizerAssistant {
    @SystemMessage(
        """
You are an assistant tasked with summarizing tables and text. \
Give a concise summary of the table or text.
As output, you MUST give me only your summary in a single string, \
Here the text chunk is:
""",
    )
    fun summarize(userMessage: String): Result<String>
}