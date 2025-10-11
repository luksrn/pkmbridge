package com.github.luksrn.pkmbridge.logseq

import dev.langchain4j.data.segment.TextSegment
import dev.langchain4j.model.embedding.EmbeddingModel
import dev.langchain4j.store.embedding.EmbeddingStore
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class LogseqEmbeddingStoreIngestorConfiguration {
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
}
