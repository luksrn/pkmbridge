package com.github.luksrn.pkmbridge

import dev.langchain4j.data.document.Document
import dev.langchain4j.data.segment.TextSegment
import dev.langchain4j.model.chat.ChatModel
import dev.langchain4j.model.embedding.EmbeddingModel
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel
import dev.langchain4j.rag.DefaultRetrievalAugmentor
import dev.langchain4j.rag.RetrievalAugmentor
import dev.langchain4j.rag.content.aggregator.ContentAggregator
import dev.langchain4j.rag.content.injector.DefaultContentInjector
import dev.langchain4j.rag.content.retriever.ContentRetriever
import dev.langchain4j.rag.query.router.DefaultQueryRouter
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore
import org.springframework.beans.factory.ObjectProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class RagConfiguration {
    @Bean
    fun embeddingModel(): EmbeddingModel = AllMiniLmL6V2EmbeddingModel()

    @Bean
    @Primary
    fun embeddingStore(): InMemoryEmbeddingStore<TextSegment?> = InMemoryEmbeddingStore<TextSegment?>()

    @Bean
    fun retrievalAugmentor(
        contentRetrievers: List<ContentRetriever>,
        contentAggregatorProvider: ObjectProvider<ContentAggregator>,
        chatModel: ChatModel,
    ): RetrievalAugmentor =
        DefaultRetrievalAugmentor
            .builder()
            .queryTransformer(OriginalAndExpandingQueryTransform(chatModel))
            .queryRouter(DefaultQueryRouter(contentRetrievers))
            .contentAggregator(contentAggregatorProvider.ifAvailable)
            .contentInjector(DefaultContentInjector(listOf<String>("pkm", Document.FILE_NAME, "link")))
            .build()
}
