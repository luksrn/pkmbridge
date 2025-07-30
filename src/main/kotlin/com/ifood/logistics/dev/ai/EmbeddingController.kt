package com.ifood.logistics.dev.ai

import com.ifood.logistics.dev.ai.pkm.Assistant
import dev.langchain4j.data.segment.TextSegment
import dev.langchain4j.model.embedding.EmbeddingModel
import dev.langchain4j.store.embedding.EmbeddingSearchRequest
import dev.langchain4j.store.embedding.EmbeddingSearchResult
import dev.langchain4j.store.embedding.EmbeddingStore
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import java.lang.StringBuilder
import kotlin.collections.forEach

@RestController
class EmbeddingController(
    val embeddingStore: EmbeddingStore<TextSegment>,
    val embeddingModel: EmbeddingModel) {

    @GetMapping("/embedding")
    @ResponseBody
    fun model(@RequestParam(value = "query", defaultValue = "Hello") q: String) : String  {
        var queryEmbedding = embeddingModel.embed(q).content()
        val embeddingSearchRequest = EmbeddingSearchRequest.builder()
            .queryEmbedding(queryEmbedding)
            .maxResults(25)
            .build()

        val matches = embeddingStore.search(embeddingSearchRequest).matches()
        val builder = StringBuilder()
        matches.forEach {
            builder.append( it.score() ).append("\n")
            builder.append( it.embedded().text() ).append("\n")
            builder.append( it.embedded().metadata() ).append("\n")
        }
        return builder.toString()
    }
}