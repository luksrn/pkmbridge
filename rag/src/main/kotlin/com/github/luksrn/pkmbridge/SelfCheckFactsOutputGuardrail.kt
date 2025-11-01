package com.github.luksrn.pkmbridge

import dev.langchain4j.guardrail.OutputGuardrail
import dev.langchain4j.guardrail.OutputGuardrailRequest
import dev.langchain4j.guardrail.OutputGuardrailResult

class SelfCheckFactsOutputGuardrail() : OutputGuardrail {

    override fun validate(request: OutputGuardrailRequest): OutputGuardrailResult {
        return success()
    }
}