package com.github.luksrn.pkmbridge.obsidian

import dev.langchain4j.store.embedding.EmbeddingStoreIngestor
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(ObsidianProperties::class)
class ObsidianRAGInitializer {
    @Bean
    @ConditionalOnProperty(name = ["pkm.obsidian.enabled"], havingValue = "true", matchIfMissing = true)
    fun obsidianRAGApplicationRunner(
        obsidianProperties: ObsidianProperties,
        @Qualifier("obsidianEmbeddingStoreIngestor") embeddingStoreIngestor: EmbeddingStoreIngestor,
    ) = ApplicationRunner { args ->
        logger.info("Initializing Obsidian RAG on path: ${obsidianProperties.fileSystemPath}")
        embeddingStoreIngestor.ingest(ObsidianMarkdownDocumentLoader(obsidianProperties.fileSystemPath).loadDocuments())
        logger.info("Loaded documents from Obsidian API and synced into embedding store.")
    }

    private val logger = LoggerFactory.getLogger(ObsidianRAGInitializer::class.java)
}
