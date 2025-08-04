package com.ifood.logistics.dev.ai.logseq

import dev.langchain4j.data.segment.TextSegment
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel
import dev.langchain4j.model.output.Response
import dev.langchain4j.model.scoring.onnx.OnnxScoringModel
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

        var query = "Ivy Lee Method is best suited for"
        var result = store.search(
            EmbeddingSearchRequest.builder()
                .queryEmbedding(model.embed(query).content())
                .build()
        )


        println(result.matches())


        val pathToModel = "/Users/lucas.farias/workspace/pocs/ms-marco-MiniLM-L6-v2/onnx/model.onnx"
        val pathToTokenizer = "/Users/lucas.farias/workspace/pocs/ms-marco-MiniLM-L6-v2/tokenizer.json"
        val scoringModel = OnnxScoringModel(pathToModel, pathToTokenizer)

        val response: Response<Double?> = scoringModel.score(query, textSegment)
        println(response)

        val response2: Response<Double?> = scoringModel.score(query, "## Best Fit For Junior Developer (Learning & Execution) Best Fit: Ivy Lee Method Why? Junior developers focus on learning, completing assigned tasks, and improving their technical skills. The Ivy Lee Method helps them prioritize a manageable number of tasks (≤6 per day) and focus on one thing at a time. It avoids overwhelming them with too many priorities while ensuring they stay productive.")
        println(response2)


    }
}