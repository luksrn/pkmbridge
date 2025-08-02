package com.ifood.logistics.dev.ai.logseq

import com.ifood.logistics.dev.ai.pkm.Assistant
import dev.langchain4j.data.segment.TextSegment
import dev.langchain4j.memory.chat.MessageWindowChatMemory
import dev.langchain4j.model.embedding.EmbeddingModel
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel
import dev.langchain4j.model.ollama.OllamaChatModel
import dev.langchain4j.rag.content.retriever.ContentRetriever
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever
import dev.langchain4j.service.AiServices
import dev.langchain4j.store.embedding.EmbeddingSearchRequest
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.stereotype.Component


@Deprecated("Use the new LogseqRAG class instead")
@Component
class LogseqRAG(
    val api: LogseqApi,
    val chatModel: OllamaChatModel) // TODO: inject chat model from configuration
    {

    fun run(args: ApplicationArguments?) {
        logger.info("Initializing LogSeq RAG")

        val documents = LogseqAPIDocumentLoader(api).loadDocuments()

        logger.info("Loaded ${documents.size} documents from Logseq API. Syncing into embedding store...")
        documents.forEach {
            println(it.text())
        }

        val embeddingStore: InMemoryEmbeddingStore<TextSegment?> = InMemoryEmbeddingStore<TextSegment?>()
        val embeddingModel: EmbeddingModel = AllMiniLmL6V2EmbeddingModel()

        EmbeddingStoreIngestor.builder()
            .embeddingStore(embeddingStore)
            .embeddingModel(embeddingModel)
            .documentSplitter(LogseqDocumentByBlockSplitter())
            //.textSegmentTransformer(LogseqTextSegmentTransformer(api))
            .build()
            .ingest(documents);

        // content retriever
        //EmbeddingStoreContentRetriever.from(embeddingStore)
        val contentRetriever: ContentRetriever = EmbeddingStoreContentRetriever.builder()
            .embeddingModel(embeddingModel)
            .embeddingStore(embeddingStore)
            .maxResults(10)
            .minScore(0.75)
            .build()


            val assistant = AiServices.builder<Assistant>(Assistant::class.java)
            .chatModel(chatModel)
            //.tools(LogseqApiTool())
            .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
            .contentRetriever(contentRetriever)
            .build()

        println("Testing embedding store search...")
        var queryEmbedding = embeddingModel.embed("Why do I need to prioritize only 9 tasks?").content()
        val embeddingSearchRequest = EmbeddingSearchRequest.builder()
            .queryEmbedding(queryEmbedding)
            .maxResults(5)
            .build()
        val matches = embeddingStore.search(embeddingSearchRequest).matches()
        matches.forEach {
            println("Match: ${it.embedded()?.text()} - Score: ${it.score()}")
        }
        println("done...")

        //println("R: ${assistant.chat("Who is Manoel Edelson? What you know about him?")}")
        //println("R: ${assistant.chat("what is logseq and how can I benefit from it?! What are the key features?")}")
        //println("R: ${assistant.chat("there is any relation of logseq with some secret?")}")
        //println("Response: ${assistant.chat("WHO is a software developer?")}")
        println("R: ${assistant.chat("I am am trying to organize the way I work as an individual and I need assistance to prioritize my tasks and activities. I am a junior software developer. can you help suggesting a method of prioritization that best fit my needs?")}")


    }

    private val logger = LoggerFactory.getLogger(LogseqRAG::class.java)

}