package com.ifood.logistics.dev.ai

import com.ifood.logistics.dev.ai.logseq.*
import com.ifood.logistics.dev.ai.pkm.Assistant
import com.ifood.logistics.dev.ai.pkm.SummarizerAssistant
import dev.langchain4j.data.segment.TextSegment
import dev.langchain4j.memory.chat.MessageWindowChatMemory
import dev.langchain4j.model.embedding.EmbeddingModel
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel
import dev.langchain4j.model.ollama.OllamaChatModel
import dev.langchain4j.model.ollama.OllamaModels
import dev.langchain4j.model.ollama.OllamaStreamingChatModel
import dev.langchain4j.rag.DefaultRetrievalAugmentor
import dev.langchain4j.rag.RetrievalAugmentor
import dev.langchain4j.rag.content.retriever.ContentRetriever
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever
import dev.langchain4j.rag.query.router.DefaultQueryRouter
import dev.langchain4j.rag.query.router.QueryRouter
import dev.langchain4j.service.AiServices
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.http.converter.json.KotlinSerializationJsonHttpMessageConverter
import java.time.Duration


@Configuration
class AssistantConfiguration {

    private val logger = LoggerFactory.getLogger(AssistantConfiguration::class.java)

    @Bean
    fun streamChatModel() = OllamaStreamingChatModel.builder()
        .baseUrl("http://localhost:11434")
        .temperature(0.7)           // temperature (between 0 and 2)
        .topP(0.95)                 // topP (between 0 and 1) — cumulative probability of the most probable tokens
        .topK(3)
        .logRequests(true)
        .logResponses(true)
        //.modelName("llama2")
        .modelName("gemma3")
        //.modelName("qwen3:8b")
        .timeout(Duration.ofSeconds(60 * 5))
        .build()


    @Bean
    fun ollamaModels(): OllamaModels {
        return OllamaModels.builder()
            .baseUrl("http://localhost:11434")
            .build()
    }
    @Bean
    fun chatModel() = OllamaChatModel.builder()
        .baseUrl("http://localhost:11434")
        .temperature(0.7)           // temperature (between 0 and 2)
        .topP(0.95)                 // topP (between 0 and 1) — cumulative probability of the most probable tokens
        .topK(3)
        .logRequests(true)
        .logResponses(true)
        //.modelName("llama2")
        .modelName("gemma3")
        //.modelName("qwen3:8b")
        .timeout(Duration.ofSeconds(60 * 5))
        .build()

    @Bean
    @Primary
    fun embeddingStoreTextSegments(): InMemoryEmbeddingStore<TextSegment?> {
        return InMemoryEmbeddingStore<TextSegment?>()
    }

    @Bean
    fun embeddingStoreTextSummaries(): InMemoryEmbeddingStore<TextSegment?> {
        return InMemoryEmbeddingStore<TextSegment?>()
    }

    @Bean
    fun embeddingModel(): EmbeddingModel {
        return AllMiniLmL6V2EmbeddingModel()
    }

    @Bean
    fun contentRetrieverTextSegments(): ContentRetriever {
        return EmbeddingStoreContentRetriever.builder()
            .embeddingModel(embeddingModel())
            .embeddingStore(embeddingStoreTextSegments())
            .maxResults(25)
            .minScore(0.70) // 0.70
            .build()
    }


    @Bean
    fun contentRetrieverTextSummaries(): ContentRetriever {
        return EmbeddingStoreContentRetriever.builder()
            .embeddingModel(embeddingModel())
            .embeddingStore(embeddingStoreTextSummaries())
            .maxResults(10)
            .minScore(0.70) // 0.70
            .build()
    }


    @Bean
    fun queryRouter() : QueryRouter  =
        DefaultQueryRouter(
            contentRetrieverTextSegments(),
            contentRetrieverTextSummaries()
        )


    @Bean
    fun retrievalAugmentor() : RetrievalAugmentor {
        return DefaultRetrievalAugmentor.builder()
            .queryRouter(queryRouter())
            .build();
    }


    @Bean
    @Primary
    fun assistant(): Assistant {
        return AiServices.builder<Assistant>(Assistant::class.java)
            .chatModel(chatModel())
            .streamingChatModel(streamChatModel())
            .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
            .retrievalAugmentor (retrievalAugmentor())
            .build()
    }


    @Bean
    fun summarizer(): SummarizerAssistant {
        return AiServices.builder<SummarizerAssistant>(SummarizerAssistant::class.java)
            .chatModel(chatModel())
            .build()
    }


    @Bean
    @Primary
    fun embeddingStoreIngestorSegments(api: LogseqApi): EmbeddingStoreIngestor {
        return EmbeddingStoreIngestor.builder()
            .embeddingStore(embeddingStoreTextSegments())
            .embeddingModel(embeddingModel())
            .documentTransformer(LogseqDocumentTransformer(api))
            .documentSplitter(LogseqDocumentByRootBlockSplitter())//LogseqDocumentByBlockSplitter())
            .build()
    }


    @Bean
    fun embeddingStoreIngestorSummaries(summarizerAssistant: SummarizerAssistant, api: LogseqApi): EmbeddingStoreIngestor {
        return EmbeddingStoreIngestor.builder()
            .embeddingStore(embeddingStoreTextSummaries())
            .embeddingModel(embeddingModel())
            .documentTransformer(LogseqDocumentSummarizedTransformer(summarizerAssistant))
            .documentSplitter(LogseqDocumentBySummarySplitter())
            .build()
    }

    @Bean
    fun initializer(embeddingStoreIngestor: EmbeddingStoreIngestor, api: LogseqApi) = ApplicationRunner { args ->
        logger.info("Initializing LogSeq RAG")
        embeddingStoreIngestor.ingest(LogseqAPIDocumentLoader(api).loadDocuments())
        logger.info("Loaded documents from Logseq API and synced into embedding store.")
    }

    @Bean
    fun initializerSummaries(@Qualifier("embeddingStoreIngestorSummaries") embeddingStoreIngestor: EmbeddingStoreIngestor, api: LogseqApi) = ApplicationRunner { args ->
        try {
            logger.info("Initializing LogSeq RAG summaries")
            embeddingStoreIngestor.ingest(LogseqAPIDocumentLoader(api).loadDocuments())
            logger.info("Loaded documents from Logseq API and synced into embedding store.")
        } catch (ex: Exception) {
            logger.error("Error initializing summaries", ex)
        }
    }

    @Bean
    fun ktxMessageConverter() : KotlinSerializationJsonHttpMessageConverter {
        // if you want to ignore unknown keys from json string,
        // otherwise make sure your data class has all json keys.
        val json = Json { ignoreUnknownKeys = true }
        return KotlinSerializationJsonHttpMessageConverter(json)
    }
}