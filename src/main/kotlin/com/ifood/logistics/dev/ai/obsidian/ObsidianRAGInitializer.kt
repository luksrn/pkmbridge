package com.ifood.logistics.dev.ai.obsidian

import dev.langchain4j.store.embedding.EmbeddingStoreIngestor
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ObsidianRAGInitializer {

    @Bean
    fun obsidianRAGApplicationRunner(
        @Value("\${pkm.obsidian.file-system-path}") vaultPath: String,
        @Qualifier("obsidianEmbeddingStoreIngestor") embeddingStoreIngestor: EmbeddingStoreIngestor) = ApplicationRunner { args ->
        logger.info("Initializing Obsidian RAG on path: $vaultPath")
        embeddingStoreIngestor.ingest(ObsidianMarkdownDocumentLoader(vaultPath).loadDocuments())
        logger.info("Loaded documents from Logseq API and synced into embedding store.")
    }

    private val logger = LoggerFactory.getLogger(ObsidianRAGInitializer::class.java)
}