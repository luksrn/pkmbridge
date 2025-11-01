package com.github.luksrn.pkmbridge

import dev.langchain4j.guardrail.InputGuardrail
import dev.langchain4j.guardrail.InputGuardrailRequest
import dev.langchain4j.guardrail.InputGuardrailResult

class RagOnlyInputGuardrail : InputGuardrail {
    override fun validate(request: InputGuardrailRequest): InputGuardrailResult {
        if (request
                .requestParams()
                .augmentationResult()
                .contents()
                .isEmpty()
        ) {
            return failure("The response does not contain any retrieved documents from RAG.")
        }

        return success()
    }
}
