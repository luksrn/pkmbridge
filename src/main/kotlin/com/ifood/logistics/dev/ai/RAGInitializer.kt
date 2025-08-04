package com.ifood.logistics.dev.ai

import com.ifood.logistics.dev.ai.logseq.LogseqAPIDocumentLoader
import com.ifood.logistics.dev.ai.logseq.LogseqApi
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RAGInitializer {


    @Bean
    fun initializer(embeddingStoreIngestor: EmbeddingStoreIngestor, api: LogseqApi) = ApplicationRunner { args ->
        logger.info("Initializing LogSeq RAG")
        embeddingStoreIngestor.ingest(LogseqAPIDocumentLoader(api).loadDocuments())
        logger.info("Loaded documents from Logseq API and synced into embedding store.")
    }

    // TODO Optimize this to work together with the main initializer
    @Bean
    fun initializerSummaries(@Qualifier("embeddingStoreIngestorSummaries") embeddingStoreIngestor: EmbeddingStoreIngestor, api: LogseqApi) = ApplicationRunner { args ->
        try {
            logger.info("Initializing LogSeq RAG summaries")
            embeddingStoreIngestor.ingest(LogseqAPIDocumentLoader(api).loadDocuments())
            logger.info("Loaded documents from Logseq API and synced into embedding store.")
        } catch (ex: Exception) {
            logger.error("Error initializing summaries", ex)
        }
    }

    private val logger = LoggerFactory.getLogger(RAGInitializer::class.java)
}