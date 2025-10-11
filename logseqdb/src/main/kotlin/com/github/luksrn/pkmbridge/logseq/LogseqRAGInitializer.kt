package com.github.luksrn.pkmbridge.logseq

import dev.langchain4j.store.embedding.EmbeddingStoreIngestor
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order

@Configuration
@ConditionalOnProperty(name = ["pkm.logseq.enabled"], havingValue = "true", matchIfMissing = true)
class LogseqRAGInitializer {

    @Deprecated("Use initializer instead")
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    fun logseqAvailabilityCheck(logRestClient: LogseqRestClient) =
        ApplicationRunner { args ->
            try {
                logRestClient.getCurrentGraph()
            } catch (ex: java.lang.Exception) {
                throw LogseqAvailabilityCheckException(ex)
            }
        }

    @Bean
    fun initializer(
        embeddingStoreIngestor: EmbeddingStoreIngestor,
        client: LogseqRestClient,
    ) = ApplicationRunner { args ->
        embeddingStoreIngestor.ingest(LogseqAPIDocumentLoader(client).loadDocuments())
    }

    private val logger = LoggerFactory.getLogger(LogseqRAGInitializer::class.java)
}
