package com.github.luksrn.pkmbridge

import dev.langchain4j.guardrail.OutputGuardrail
import dev.langchain4j.guardrail.OutputGuardrailRequest
import dev.langchain4j.guardrail.OutputGuardrailResult
import dev.langchain4j.model.chat.ChatModel
import dev.langchain4j.model.input.PromptTemplate
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

/**
 * An output guardrail that performs a self-check to verify if the generated response is
 * grounded and entailed by the provided evidence.
 *
 * It uses a chat model to evaluate whether the response is supported by the evidence.
 * If the response is not entailed by the evidence, it returns a failure result.
 *
 * ref: https://docs.nvidia.com/nemo/guardrails/latest/user-guides/guardrails-library.html#fact-checking
 */
@Component
@ConditionalOnProperty(name = ["guardrails.output.check-facts.enabled"], havingValue = "true", matchIfMissing = false)
class SelfCheckFactsOutputGuardrail(
    val chatModel: ChatModel,
) : OutputGuardrail {
    val defaultTemplate: PromptTemplate =
        PromptTemplate.from(
            """
            You are given a task to identify if the hypothesis is grounded and entailed to the evidence.
            You will only use the contents of the evidence and not rely on external knowledge.
            Answer [Yes/No] "evidence": {{evidence}} "hypothesis": {{response}} entails.
            """.trimIndent(),
        )

    override fun validate(output: OutputGuardrailRequest): OutputGuardrailResult {
        val prompt =
            defaultTemplate.apply(
                mapOf(
                    "evidence" to
                        output
                            .requestParams()
                            .augmentationResult()
                            .contents()
                            .joinToString("\n") { it.textSegment().text() },
                    "response" to output.responseFromLLM().aiMessage().text(),
                ),
            )
        val response = chatModel.chat(prompt.text())
        return parseResponse(response)
    }

    private fun parseResponse(responseText: String): OutputGuardrailResult {
        val normalizedResponse = responseText.trim().lowercase()
        if (normalizedResponse.contains("yes")) {
            return failure("Failed to pass self-check input that checks user input against harmful or inappropriate content.")
            // return OutputGuardrailResult.success()
        }
        return failure("Failed to pass self-check input that checks user input against harmful or inappropriate content.")
    }
}
