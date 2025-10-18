package com.github.luksrn.pkmbridge

import com.github.luksrn.pkmbridge.logseqdb.LogseqAPIDocumentLoader
import com.github.luksrn.pkmbridge.logseqdb.LogseqDocumentByRootBlockSplitter
import com.github.luksrn.pkmbridge.logseqdb.LogseqDocumentTransformer
import com.github.luksrn.pkmbridge.logseqdb.LogseqRestClient
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
