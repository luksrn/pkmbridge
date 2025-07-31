package com.ifood.logistics.dev.ai

import dev.langchain4j.data.message.UserMessage
import dev.langchain4j.data.segment.TextSegment
import dev.langchain4j.model.embedding.EmbeddingModel
import dev.langchain4j.rag.AugmentationRequest
import dev.langchain4j.rag.RetrievalAugmentor
import dev.langchain4j.store.embedding.EmbeddingSearchRequest
import dev.langchain4j.store.embedding.EmbeddingStore
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
class EmbeddingController(
    val retrivalAugmentor: RetrievalAugmentor,
    val embeddingStore: EmbeddingStore<TextSegment>,
    val embeddingModel: EmbeddingModel) {

    @GetMapping("/embedding")
    @ResponseBody
    fun model(@RequestParam(value = "query", defaultValue = "Hello") q: String) : String  {

        val metadata = dev.langchain4j.rag.query.Metadata.from(UserMessage(q), null, null)
        val augmentationRequest = AugmentationRequest(UserMessage(q), metadata)

        var result = retrivalAugmentor.augment(augmentationRequest)

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