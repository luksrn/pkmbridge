package com.github.luksrn.pkmbridge.logseqdb

import dev.langchain4j.data.segment.TextSegment
import dev.langchain4j.model.embedding.EmbeddingModel
import dev.langchain4j.rag.content.retriever.ContentRetriever
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever
import dev.langchain4j.store.embedding.EmbeddingStore
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import java.lang.Exception

@Configuration
@ConditionalOnProperty(name = ["pkm.logseq.enabled"], havingValue = "true", matchIfMissing = true)
class LogseqConfiguration {
    @Bean
    fun initializer(
        embeddingStoreIngestor: EmbeddingStoreIngestor,
        client: LogseqRestClient,
    ) = ApplicationRunner { args ->
        embeddingStoreIngestor.ingest(LogseqAPIDocumentLoader(client).loadDocuments())
    }

    @Bean
    @Primary
    fun logseqEmbeddingStoreIngestor(
        embeddingModel: EmbeddingModel,
        embeddingStore: EmbeddingStore<TextSegment>,
        client: LogseqRestClient,
    ): EmbeddingStoreIngestor =
        EmbeddingStoreIngestor
            .builder()
            .embeddingStore(embeddingStore)
            .embeddingModel(embeddingModel)
            .documentTransformer(LogseqDocumentTransformer(client))
            .documentSplitter(LogseqDocumentByRootBlockSplitter())
            .build()

    @Bean
    fun logseqContentRetriever(
        embeddingModel: EmbeddingModel,
        embeddingStore: EmbeddingStore<TextSegment>,
    ): ContentRetriever =
        EmbeddingStoreContentRetriever
            .builder()
            .displayName("LogseqDB Content Retriever")
            .embeddingModel(embeddingModel)
            .embeddingStore(embeddingStore)
            .maxResults(50)
            .minScore(0.70) // 0.70
            .build()

    @Deprecated("Use initializer instead")
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    fun logseqAvailabilityCheck(logRestClient: LogseqRestClient) =
        ApplicationRunner { args ->
            try {
                logRestClient.getCurrentGraph()
            } catch (ex: Exception) {
                throw LogseqAvailabilityCheckException(ex)
            }
        }
}
