package com.ifood.logistics.dev.ai.obsidian

import dev.langchain4j.data.document.splitter.DocumentSplitters
import dev.langchain4j.data.segment.TextSegment
import dev.langchain4j.model.embedding.EmbeddingModel
import dev.langchain4j.store.embedding.EmbeddingStore
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class ObsidianConfiguration {

    @Bean
    fun obsidianEmbeddingStoreIngestor(embeddingModel: EmbeddingModel,
          embeddingStore: EmbeddingStore<TextSegment>) = EmbeddingStoreIngestor.builder()
            .documentTransformer(ObsidianDocumentTransformer())
            .documentSplitter(DocumentSplitters.recursive(1000, 100))
            .embeddingModel(embeddingModel)
            .embeddingStore(embeddingStore)
            .build()

}