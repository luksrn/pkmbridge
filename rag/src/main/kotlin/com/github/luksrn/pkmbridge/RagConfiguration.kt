package com.github.luksrn.pkmbridge

import dev.langchain4j.data.document.Document
import dev.langchain4j.data.segment.TextSegment
import dev.langchain4j.internal.Exceptions
import dev.langchain4j.model.chat.ChatModel
import dev.langchain4j.model.embedding.EmbeddingModel
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel
import dev.langchain4j.model.scoring.onnx.OnnxScoringModel
import dev.langchain4j.rag.DefaultRetrievalAugmentor
import dev.langchain4j.rag.RetrievalAugmentor
import dev.langchain4j.rag.content.Content
import dev.langchain4j.rag.content.aggregator.ContentAggregator
import dev.langchain4j.rag.content.aggregator.ReRankingContentAggregator
import dev.langchain4j.rag.content.injector.DefaultContentInjector
import dev.langchain4j.rag.content.retriever.ContentRetriever
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever
import dev.langchain4j.rag.query.Query
import dev.langchain4j.rag.query.router.DefaultQueryRouter
import dev.langchain4j.rag.query.router.QueryRouter
import dev.langchain4j.rag.query.transformer.ExpandingQueryTransformer
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component
import java.util.function.Function

@Configuration
class RagConfiguration {
    @Bean
    fun embeddingModel(): EmbeddingModel = AllMiniLmL6V2EmbeddingModel()

    @Bean
    @Primary
    fun embeddingStore(): InMemoryEmbeddingStore<TextSegment?> = InMemoryEmbeddingStore<TextSegment?>()

    @Bean
    fun contentRetrievers(): ContentRetriever =
        EmbeddingStoreContentRetriever
            .builder()
            .embeddingModel(embeddingModel())
            .embeddingStore(embeddingStore())
            .maxResults(50)
            .minScore(0.70) // 0.70
            .build()

    @Bean
    fun queryRouter(contentRetrievers: List<ContentRetriever>): QueryRouter = DefaultQueryRouter(contentRetrievers)

    @Bean
    fun retrievalAugment(
        queryRouter: QueryRouter,
        contentAggregatorProvider: ObjectProvider<ContentAggregator>,
        chatModel: ChatModel
    ): RetrievalAugmentor =
        DefaultRetrievalAugmentor
            .builder()
            .queryTransformer(ExpandingQueryTransformer(chatModel))
            .queryRouter(queryRouter)
            // when re-rank is enabled, the contentAggregatorProvider will provide the ReRankingContentAggregator bean
            .contentAggregator(contentAggregatorProvider.ifAvailable)
            .contentInjector(DefaultContentInjector(listOf<String>("link", Document.FILE_NAME)))
            .build()

    @Bean
    @ConditionalOnProperty(prefix = "re-rank", name = ["enabled"], havingValue = "true", matchIfMissing = false)
    @ConditionalOnProperty(prefix = "re-rank", name = ["path-to-model", "path-to-tokenizer"])
    fun contentAggregator(reRankProperties: ReRankProperties): ContentAggregator =
        ReRankingContentAggregator
            .builder()
            .querySelector(Function { queryToContents: MutableMap<Query, MutableCollection<MutableList<Content>>> ->
                // select the query that retrieved the largest number of contents
                var selected = queryToContents.keys.first()
                for( query in queryToContents) {
                    if( query.value.first().size > queryToContents[selected]!!.first().size ) {
                        selected = query.key
                    }
                }
                selected
            })
            .scoringModel(OnnxScoringModel(reRankProperties.pathToModel, reRankProperties.pathToTokenizer))
            .maxResults(reRankProperties.maxResult)
            .minScore(reRankProperties.minScore)
            .build()
}

@Component
@ConfigurationProperties(prefix = "re-rank")
data class ReRankProperties(
    var enabled: Boolean = false,
    var pathToModel: String? = null,
    var pathToTokenizer: String? = null,
    var maxResult: Int = 20,
    var minScore: Double = 0.25,
)
