package com.ifood.logistics.dev.ai.logseq

import dev.langchain4j.store.embedding.EmbeddingStoreIngestor
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import java.net.ConnectException

@Configuration
class LogseqRAGInitializer {

    @Bean
    @ConditionalOnProperty(name = ["pkm.logseq.rag.enabled"], havingValue = "true", matchIfMissing = true)
    fun initializer(embeddingStoreIngestor: EmbeddingStoreIngestor, api: LogseqApi) = ApplicationRunner { args ->

        try {
            logger.info("Initializing LogSeq RAG")
            embeddingStoreIngestor.ingest(LogseqAPIDocumentLoader(api).loadDocuments())
            logger.info("Loaded documents from Logseq API and synced into embedding store.")
        } catch (ex: ConnectException) {
            logger.error("Can't connect to Logseq server. Ensure your server is up and running")
            throw ex
        }

    }

    @Bean
    @Order(Integer.MAX_VALUE)
    @ConditionalOnProperty(name = ["pkm.rag.summaries.enabled"], havingValue = "true", matchIfMissing = true)
    fun initializerSummaries(@Qualifier("embeddingStoreIngestorSummaries") embeddingStoreIngestor: EmbeddingStoreIngestor, api: LogseqApi) =
        ApplicationRunner { args ->
            try {
                logger.info("Initializing LogSeq RAG summaries")
                embeddingStoreIngestor.ingest(LogseqAPIDocumentLoader(api).loadDocuments())
                logger.info("Summaries synced into embedding store.")
            } catch (ex: Exception) {
                logger.error("Error initializing summaries", ex)
            }
        }

    private val logger = LoggerFactory.getLogger(LogseqRAGInitializer::class.java)
}