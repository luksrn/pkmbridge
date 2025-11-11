package com.github.luksrn.pkmbridge.obsidian

import dev.langchain4j.data.document.splitter.DocumentSplitters
import dev.langchain4j.data.segment.TextSegment
import dev.langchain4j.model.embedding.EmbeddingModel
import dev.langchain4j.rag.content.retriever.ContentRetriever
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever
import dev.langchain4j.store.embedding.EmbeddingStore
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
@ConditionalOnProperty(name = ["pkm.obsidian.enabled"], havingValue = "true")
@ConditionalOnProperty(name = ["pkm.obsidian.file-system-path"])
class ObsidianConfiguration {
    @Bean
    fun obsidianRAGApplicationRunner(
        obsidianProperties: ObsidianProperties,
        @Qualifier("obsidianEmbeddingStoreIngestor") embeddingStoreIngestor: EmbeddingStoreIngestor,
    ) = ApplicationRunner { args ->
        logger.info("Initializing Obsidian RAG on path: ${obsidianProperties.fileSystemPath}")
        embeddingStoreIngestor.ingest(ObsidianMarkdownDocumentLoader(obsidianProperties.fileSystemPath).loadDocuments())
        logger.info("Loaded documents from Obsidian API and synced into embedding store.")
    }

    @Bean
    fun obsidianEmbeddingStoreIngestor(
        embeddingModel: EmbeddingModel,
        embeddingStore: EmbeddingStore<TextSegment>,
        obsidianProperties: ObsidianProperties,
    ) = EmbeddingStoreIngestor
        .builder()
        .documentTransformer(ObsidianDocumentTransformer(obsidianProperties))
        .documentSplitter(DocumentSplitters.recursive(1000, 100))
        .embeddingModel(embeddingModel)
        .embeddingStore(embeddingStore)
        .build()

    @Bean
    fun obsidianContentRetriever(
        embeddingModel: EmbeddingModel,
        embeddingStore: EmbeddingStore<TextSegment>,
    ): ContentRetriever =
        EmbeddingStoreContentRetriever
            .builder()
            .displayName("Obsidian Content Retriever")
            .embeddingModel(embeddingModel)
            .embeddingStore(embeddingStore)
            .maxResults(50)
            .minScore(0.70) // 0.70
            .build()

    private val logger = LoggerFactory.getLogger(ObsidianConfiguration::class.java)
}
