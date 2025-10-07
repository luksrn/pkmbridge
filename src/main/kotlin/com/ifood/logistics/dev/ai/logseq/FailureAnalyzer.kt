package com.ifood.logistics.dev.ai.logseq

import org.springframework.boot.diagnostics.AbstractFailureAnalyzer
import org.springframework.boot.diagnostics.FailureAnalysis
import java.net.ConnectException


class FailureAnalyzer : AbstractFailureAnalyzer<ConnectException>() {

    override fun analyze(
        rootFailure: Throwable?,
        cause: ConnectException?
    ): FailureAnalysis? {
        if(cause !is ConnectException) {
            return null
        }
        return FailureAnalysis(
            "Failed to connect to Logseq Server API",
            "Logseq Server connection failed. Please confirm that the server is running and that the pkm.logseq.serverUrl property is correctly configured",
            cause
        )
    }
}