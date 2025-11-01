package com.github.luksrn.pkmbridge

import dev.langchain4j.guardrail.InputGuardrail
import dev.langchain4j.guardrail.InputGuardrailRequest
import dev.langchain4j.guardrail.InputGuardrailResult
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component

@Component
@ConditionalOnProperty(name = ["guardrails.input.rag-only.enabled"], havingValue = "true", matchIfMissing = true)
class RagOnlyInputGuardrail : InputGuardrail {
    override fun validate(request: InputGuardrailRequest): InputGuardrailResult {
        if (request
                .requestParams()
                .augmentationResult()
                .contents()
                .isEmpty()
        ) {
            return fatal("The response does not contain any retrieved documents from RAG.")
        }

        return success()
    }
}
