package com.github.luksrn.pkmbridge.logseq

import dev.langchain4j.store.embedding.EmbeddingStoreIngestor
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order

@Configuration
@ConditionalOnProperty(name = ["pkm.logseq.enabled"], havingValue = "true", matchIfMissing = true)
class LogseqRAGInitializer {
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
        logger.info("Initializing LogSeq RAG")
        embeddingStoreIngestor.ingest(LogseqAPIDocumentLoader(client).loadDocuments())
        logger.info("Loaded documents from Logseq API and synced into embedding store.")
    }

    @Bean
    @Order(Ordered.LOWEST_PRECEDENCE)
    @ConditionalOnProperty(name = ["pkm.rag.summaries.enabled"], havingValue = "true", matchIfMissing = true)
    fun initializerSummaries(
        @Qualifier("embeddingStoreIngestorSummaries") embeddingStoreIngestor: EmbeddingStoreIngestor,
        client: LogseqRestClient,
    ) = ApplicationRunner { args ->
        try {
            logger.info("Initializing LogSeq RAG summaries")
            embeddingStoreIngestor.ingest(LogseqAPIDocumentLoader(client).loadDocuments())
            logger.info("Summaries synced into embedding store.")
        } catch (ex: Exception) {
            logger.error("Error initializing summaries", ex)
        }
    }

    private val logger = LoggerFactory.getLogger(LogseqRAGInitializer::class.java)
}
