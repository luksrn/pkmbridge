package com.github.luksrn.pkmbridge.logseqdb

import dev.langchain4j.data.segment.TextSegment
import dev.langchain4j.model.embedding.EmbeddingModel
import dev.langchain4j.rag.content.retriever.ContentRetriever
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever
import dev.langchain4j.store.embedding.EmbeddingStore
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class LogseqRagConfiguration {
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
            .documentSplitter(LogseqDocumentByRootBlockSplitter()) // LogseqDocumentByBlockSplitter())
            .build()

    @Bean
    fun logseqContentRetriever(
        embeddingModel: EmbeddingModel,
        embeddingStore: EmbeddingStore<TextSegment>,
    ): ContentRetriever =
        EmbeddingStoreContentRetriever
            .builder()
            .displayName("Logseq Content Retriever")
            .embeddingModel(embeddingModel)
            .embeddingStore(embeddingStore)
            .maxResults(50)
            .minScore(0.70) // 0.70
            .build()
}
