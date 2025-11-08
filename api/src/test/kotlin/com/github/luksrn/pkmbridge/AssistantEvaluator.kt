package com.github.luksrn.pkmbridge

import dev.langchain4j.model.chat.ChatModel
import dev.langchain4j.model.input.PromptTemplate

// https://github.com/spring-projects/spring-ai/blob/main/spring-ai-client-chat/src/main/java/org/springframework/ai/chat/evaluation/RelevancyEvaluator.java#L35
class AssistantEvaluator(
    val chatModel: ChatModel,
) {
    val defaultTemplate: PromptTemplate =
        PromptTemplate.from(
            """
            Your task is to evaluate if the response for the query
            is in line with the context information provided.

            You have two options to answer. Either YES or NO.

            Answer YES, if the response for the query
            is in line with context information otherwise NO.

            Query:
            {{query}}

            Response:
            {{response}}

            Context:
            {{context}}

            Answer:
            """.trimIndent(),
        )

    fun evaluate(output: EvaluationRequest): EvaluationResult {
        val prompt =
            defaultTemplate.apply(
                mapOf(
                    "query" to output.prompt,
                    "response" to output.response,
                    "context" to output.context,
                ),
            )
        val response = chatModel.chat(prompt.text())
        return parseResponse(response)
    }

    private fun parseResponse(responseText: String): EvaluationResult {
        val normalizedResponse = responseText.trim().lowercase()
        if (normalizedResponse.contains("yes")) {
            return EvaluationResult(true)
        }
        return EvaluationResult(false)
    }
}

data class EvaluationRequest(
    val prompt: String,
    val response: String,
    val context: String,
)

data class EvaluationResult(
    val pass: Boolean,
)
