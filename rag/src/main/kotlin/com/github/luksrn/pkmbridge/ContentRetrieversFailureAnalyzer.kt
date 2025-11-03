package com.github.luksrn.pkmbridge

import org.springframework.beans.factory.BeanCreationException
import org.springframework.beans.factory.UnsatisfiedDependencyException
import org.springframework.boot.diagnostics.AbstractFailureAnalyzer
import org.springframework.boot.diagnostics.FailureAnalysis

class ContentRetrieversFailureAnalyzer : AbstractFailureAnalyzer<IllegalArgumentException>() {
    override fun analyze(
        rootFailure: Throwable,
        cause: IllegalArgumentException,
    ): FailureAnalysis? {

        if(rootFailure is UnsatisfiedDependencyException) {
            val beanCreationException = rootFailure.cause as? BeanCreationException
            if (beanCreationException?.message!!.contains("contentRetrievers cannot be null or empty")) {
                return FailureAnalysis(
                    "At lest one document loader should be provided to create Content Retrievers.",
                    "Please, enable and configure at least one PKM to be used as a RAG. eg: `pkm.logseq.enabled=true` property.",
                    cause,
                )
            }
        }
        return null
    }
}
