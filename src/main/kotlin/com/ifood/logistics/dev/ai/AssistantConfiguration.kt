package com.ifood.logistics.dev.ai

import com.ifood.logistics.dev.ai.logseq.LogseqAPIDocumentLoader
import com.ifood.logistics.dev.ai.logseq.LogseqApi
import com.ifood.logistics.dev.ai.logseq.LogseqDocumentByBlockSplitter
import com.ifood.logistics.dev.ai.logseq.LogseqDocumentByRootBlockSplitter
import com.ifood.logistics.dev.ai.logseq.LogseqDocumentTransformer
import com.ifood.logistics.dev.ai.logseq.LogseqRAG
import com.ifood.logistics.dev.ai.logseq.LogseqTextSegmentTransformer
import com.ifood.logistics.dev.ai.pkm.Assistant
import dev.langchain4j.data.segment.TextSegment
import dev.langchain4j.memory.chat.MessageWindowChatMemory
import dev.langchain4j.model.embedding.EmbeddingModel
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel
import dev.langchain4j.model.ollama.OllamaChatModel
import dev.langchain4j.rag.DefaultRetrievalAugmentor
import dev.langchain4j.rag.content.injector.DefaultContentInjector
import dev.langchain4j.rag.content.retriever.ContentRetriever
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever
import dev.langchain4j.rag.query.router.DefaultQueryRouter
import dev.langchain4j.service.AiServices
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor
import dev.langchain4j.store.embedding.filter.Filter
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
class AssistantConfiguration {

    private val logger = LoggerFactory.getLogger(LogseqRAG::class.java)

    @Bean
    fun chatModel() = OllamaChatModel.builder()
        .baseUrl("http://localhost:11434")
        .temperature(0.7)           // temperature (between 0 and 2)
        .topP(0.95)                 // topP (between 0 and 1) — cumulative probability of the most probable tokens
        .topK(3)
        .logRequests(true)
        .logResponses(true)
        .modelName("gemma3")
        //.modelName("qwen3:8b")
        .timeout(Duration.ofSeconds(60 * 5))
        .build()

    @Bean
    fun embeddingStore(): InMemoryEmbeddingStore<TextSegment?> {
        return InMemoryEmbeddingStore<TextSegment?>()
    }

    @Bean
    fun embeddingModel(): EmbeddingModel {
        return AllMiniLmL6V2EmbeddingModel()
    }

    @Bean
    fun embeddingStoreIngestor(api: LogseqApi): EmbeddingStoreIngestor {
        return EmbeddingStoreIngestor.builder()
            .embeddingStore(embeddingStore())
            .embeddingModel(embeddingModel())
            .documentTransformer(LogseqDocumentTransformer())
            .documentSplitter(LogseqDocumentByRootBlockSplitter())//LogseqDocumentByBlockSplitter())
            .textSegmentTransformer(LogseqTextSegmentTransformer(api))
            .build()
    }

    @Bean
    fun contentRetriever(): ContentRetriever {
        return EmbeddingStoreContentRetriever.builder()
            .embeddingModel(embeddingModel())
            .embeddingStore(embeddingStore())
            .maxResults(25)
            .minScore(0.50) // 0.70
            .build()
    }

    @Bean
    fun assistant(): Assistant {
        return AiServices.builder<Assistant>(Assistant::class.java)
            .chatModel(chatModel())
            .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
            .contentRetriever(contentRetriever())
            .build()
    }

    @Bean
    fun initializer(embeddingStoreIngestor: EmbeddingStoreIngestor, api: LogseqApi) = ApplicationRunner { args ->
        logger.info("Initializing LogSeq RAG")
        embeddingStoreIngestor.ingest(LogseqAPIDocumentLoader(api).loadDocuments())
        logger.info("Loaded documents from Logseq API and synced into embedding store.")
    }
}