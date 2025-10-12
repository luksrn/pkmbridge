package com.github.luksrn.pkmbridge.logseqdb

import org.springframework.boot.diagnostics.AbstractFailureAnalyzer
import org.springframework.boot.diagnostics.FailureAnalysis
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.ResourceAccessException

class LogseqAvailabilityCheckException(
    cause: Throwable? = null,
) : RuntimeException(cause)

class LogseqAvailabilityFailureAnalyzer : AbstractFailureAnalyzer<LogseqAvailabilityCheckException>() {
    override fun analyze(
        rootFailure: Throwable,
        cause: LogseqAvailabilityCheckException,
    ): FailureAnalysis {
        when (cause.cause) {
            is ResourceAccessException -> {
                return FailureAnalysis(
                    "Failed to connect to Logseq Server API. Root cause: ${cause.cause?.message}",
                    "Please confirm that the server is running and that the `pkm.logseq.serverUrl` property is correctly configured",
                    cause,
                )
            }
            is HttpClientErrorException.NotFound -> {
                val httpEx = cause.cause as HttpClientErrorException
                return FailureAnalysis(
                    "Failed to connect to Logseq Server API",
                    "Logseq Server connection failed with status ${httpEx.statusCode}. Please confirm that the server is running and that the pkm.logseq.serverUrl property is correctly configured",
                    cause,
                )
            }
            is HttpClientErrorException.Unauthorized -> {
                return FailureAnalysis(
                    "authorization-token is not valid or missing",
                    "Check if you configure a `pkm.logseq.authorization-token` property is correctly configured",
                    cause,
                )
            }
            else -> {
                return FailureAnalysis(
                    "Failed to connect to Logseq Server API",
                    "Logseq Server connection failed. Please confirm that the server is running and that the `pkm.logseq.*` properties is correctly configured",
                    cause,
                )
            }
        }
    }
}
