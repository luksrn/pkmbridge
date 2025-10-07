package com.ifood.logistics.dev.ai

import com.ifood.logistics.dev.ai.logseq.*
import com.ifood.logistics.dev.ai.ollama.OllamaProperties
import com.ifood.logistics.dev.ai.pkm.Assistant
import com.ifood.logistics.dev.ai.pkm.SummarizerAssistant
import dev.langchain4j.data.document.Document
import dev.langchain4j.data.segment.TextSegment
import dev.langchain4j.memory.chat.ChatMemoryProvider
import dev.langchain4j.memory.chat.MessageWindowChatMemory
import dev.langchain4j.model.chat.ChatModel
import dev.langchain4j.model.chat.StreamingChatModel
import dev.langchain4j.model.embedding.EmbeddingModel
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel
import dev.langchain4j.model.scoring.onnx.OnnxScoringModel
import dev.langchain4j.rag.DefaultRetrievalAugmentor
import dev.langchain4j.rag.RetrievalAugmentor
import dev.langchain4j.rag.content.aggregator.ContentAggregator
import dev.langchain4j.rag.content.aggregator.ReRankingContentAggregator
import dev.langchain4j.rag.content.injector.DefaultContentInjector
import dev.langchain4j.rag.content.retriever.ContentRetriever
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever
import dev.langchain4j.rag.query.router.DefaultQueryRouter
import dev.langchain4j.rag.query.router.QueryRouter
import dev.langchain4j.service.AiServices
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore
import dev.langchain4j.store.memory.chat.InMemoryChatMemoryStore
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component

@Configuration
class AssistantConfiguration(
    val ollamaProperties: OllamaProperties,
) {
    @Bean
    @Primary
    fun embeddingStoreTextSegments(): InMemoryEmbeddingStore<TextSegment?> = InMemoryEmbeddingStore<TextSegment?>()

    @Bean
    fun embeddingStoreTextSummaries(): InMemoryEmbeddingStore<TextSegment?> = InMemoryEmbeddingStore<TextSegment?>()

    @Bean
    fun embeddingModel(): EmbeddingModel = AllMiniLmL6V2EmbeddingModel()

    @Bean
    fun contentRetrieverTextSegments(): ContentRetriever =
        EmbeddingStoreContentRetriever
            .builder()
            .embeddingModel(embeddingModel())
            .embeddingStore(embeddingStoreTextSegments())
            .maxResults(25)
            .minScore(0.70) // 0.70
            .build()

    @Bean
    fun contentRetrieverTextSummaries(): ContentRetriever =
        EmbeddingStoreContentRetriever
            .builder()
            .embeddingModel(embeddingModel())
            .embeddingStore(embeddingStoreTextSummaries())
            .maxResults(10)
            .minScore(0.70) // 0.70
            .build()

    @Bean
    fun queryRouter(): QueryRouter =
        DefaultQueryRouter(
            contentRetrieverTextSegments(),
            contentRetrieverTextSummaries(),
        )

    @Bean
    @ConditionalOnProperty(prefix = "re-rank", name = ["path-to-model", "path-to-tokenizer"])
    fun contentAggregator(reRankProperties: ReRankProperties): ContentAggregator =
        ReRankingContentAggregator
            .builder()
            .scoringModel(OnnxScoringModel(reRankProperties.pathToModel, reRankProperties.pathToTokenizer))
            .maxResults(reRankProperties.maxResult)
            .minScore(reRankProperties.minScore)
            .build()

    @Bean
    fun retrievalAugment(contentAggregatorProvider: ObjectProvider<ContentAggregator>): RetrievalAugmentor =
        DefaultRetrievalAugmentor
            .builder()
            .queryRouter(queryRouter())
            .contentInjector(DefaultContentInjector(listOf<String>("link", Document.FILE_NAME)))
            .contentAggregator(contentAggregatorProvider.ifAvailable)
            .build()

    @Bean
    fun chatMemory(): MessageWindowChatMemory =
        MessageWindowChatMemory
            .builder()
            .maxMessages(10)
            .chatMemoryStore(InMemoryChatMemoryStore())
            .build()

    @Bean
    fun chatMemoryProvider(): ChatMemoryProvider =
        ChatMemoryProvider { memoryId ->
            MessageWindowChatMemory
                .builder()
                .id(memoryId)
                .maxMessages(10)
                .chatMemoryStore(InMemoryChatMemoryStore())
                .build()
        }

    @Bean
    @Primary
    fun assistant(
        chatModel: ChatModel,
        streamChatModel: StreamingChatModel,
        retrievalAugmentor: RetrievalAugmentor,
    ): Assistant =
        AiServices
            .builder<Assistant>(Assistant::class.java)
            .chatModel(chatModel)
            .streamingChatModel(streamChatModel)
            .chatMemoryProvider(chatMemoryProvider())
            .retrievalAugmentor(retrievalAugmentor)
            .tools(LogseqApiTool())
            .build()

    @Bean
    fun summarizer(chatModel: ChatModel): SummarizerAssistant =
        AiServices
            .builder<SummarizerAssistant>(SummarizerAssistant::class.java)
            .chatModel(chatModel)
            .build()

    @Bean
    @Primary
    fun embeddingStoreIngestorSegments(api: LogseqApi): EmbeddingStoreIngestor =
        EmbeddingStoreIngestor
            .builder()
            .embeddingStore(embeddingStoreTextSegments())
            .embeddingModel(embeddingModel())
            .documentTransformer(LogseqDocumentTransformer(api))
            .documentSplitter(LogseqDocumentByRootBlockSplitter()) // LogseqDocumentByBlockSplitter())
            .build()

    @Bean
    fun embeddingStoreIngestorSummaries(
        summarizerAssistant: SummarizerAssistant,
        api: LogseqApi,
    ): EmbeddingStoreIngestor =
        EmbeddingStoreIngestor
            .builder()
            .embeddingStore(embeddingStoreTextSummaries())
            .embeddingModel(embeddingModel())
            .documentTransformer(LogseqDocumentSummarizedTransformer(summarizerAssistant))
            .documentSplitter(LogseqDocumentBySummarySplitter())
            .build()
}

@Component
@ConfigurationProperties(prefix = "re-rank")
data class ReRankProperties(
    var enabled: Boolean = false,
    var pathToModel: String? = null,
    var pathToTokenizer: String? = null,
    var maxResult: Int = 10,
    var minScore: Double = 0.25,
)
