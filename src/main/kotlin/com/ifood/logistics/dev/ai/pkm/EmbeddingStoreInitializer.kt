package com.ifood.logistics.dev.ai.pkm

import dev.langchain4j.agent.tool.ToolSpecifications
import dev.langchain4j.data.segment.TextSegment
import dev.langchain4j.memory.chat.MessageWindowChatMemory
import dev.langchain4j.model.chat.ChatModel
import dev.langchain4j.model.ollama.OllamaChatModel
import dev.langchain4j.rag.content.retriever.ContentRetriever
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever
import dev.langchain4j.service.AiServices
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import java.time.Duration


@Component
class EmbeddingStoreInitializer : ApplicationRunner {

//
//    val chatModel: ChatModel = OllamaChatModel.builder()
//        .baseUrl("http://localhost:11434")
//        .temperature(0.2)
//        .logRequests(true)
//        .logResponses(true)
//        //.modelName("gemma3")
//        .modelName("qwen3:8b")
//        .timeout(Duration.ofSeconds(60 * 5))
//        .build()

    override fun run(args: ApplicationArguments?) {
        //val notes = LogseqDocumentLoader.loadDocuments("/Users/lucas.farias/logseq-work")
//        logger.info("Loaded ${notes.size} notes. Sync into embedding store...")
//
//        val embeddingStore: InMemoryEmbeddingStore<TextSegment?> = InMemoryEmbeddingStore<TextSegment?>()
//
//        EmbeddingStoreIngestor.builder()
//                .embeddingStore(embeddingStore)
//            .build()
//            .ingest(notes);
//
//        // content retriever
//        //EmbeddingStoreContentRetriever.from(embeddingStore)
//        val contentRetriever: ContentRetriever = EmbeddingStoreContentRetriever.builder()
//            .embeddingStore(embeddingStore)
//            .maxResults(5)
//            .minScore(0.75)
//            .build()
//
//        val toolSpecifications = ToolSpecifications.toolSpecificationsFrom(LogseqApiTool::class.java)
//
//        val assistant = AiServices.builder<Assistant>(Assistant::class.java)
//            .chatModel(chatModel)
//            .tools(LogseqApiTool())
//            .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
//            .contentRetriever(contentRetriever)
//            .build()
//
//        println("Response: ${assistant.feedbacks("Quais feedbacks eu tenho para o #torres.gabriel nos últimos 3 meses??")}")
    }

    private val logger = LoggerFactory.getLogger(EmbeddingStoreInitializer::class.java)

}