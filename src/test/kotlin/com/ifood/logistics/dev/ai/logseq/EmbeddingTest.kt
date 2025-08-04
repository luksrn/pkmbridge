package com.ifood.logistics.dev.ai.logseq

import dev.langchain4j.data.segment.TextSegment
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel
import dev.langchain4j.store.embedding.EmbeddingSearchRequest
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore
import org.junit.jupiter.api.Test

class EmbeddingTest {

    @Test
    fun testEmbeddings() {
        val textSegment = "The Ivy Lee Method is a simple task management system that involves listing six prioritized tasks for the next day and focusing on completing one at a time, ideal for junior developers who benefit from a structured approach to maximize productivity and minimize decision fatigue."

        var model = AllMiniLmL6V2EmbeddingModel()

        var embed = model.embed(textSegment)

        var store = InMemoryEmbeddingStore<TextSegment?>()
        store.add(embed.content())

        var result = store.search(
            EmbeddingSearchRequest.builder()
                .queryEmbedding(model.embed("Ivy Lee Method is best suited for").content())
                .build()
        )


        println(result.matches())

    }
}