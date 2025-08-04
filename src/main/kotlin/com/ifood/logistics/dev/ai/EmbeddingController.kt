package com.ifood.logistics.dev.ai

import dev.langchain4j.data.document.Document
import dev.langchain4j.data.message.UserMessage
import dev.langchain4j.rag.AugmentationRequest
import dev.langchain4j.rag.RetrievalAugmentor
import dev.langchain4j.rag.content.ContentMetadata
import dev.langchain4j.rag.query.Metadata
import kotlinx.serialization.Serializable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
class EmbeddingController(
    val retrivalAugmentor: RetrievalAugmentor) {

    @GetMapping("/embedding")
    @ResponseBody
    fun embeddings(@RequestParam(value = "query") text: String) : List<EmbeddingContent> {

        val metadata = Metadata.from(UserMessage(text), null, null)
        val augmentationRequest = AugmentationRequest(UserMessage(text), metadata)

        return retrivalAugmentor.augment(augmentationRequest)
            .contents()
            .map { EmbeddingContent(
                it.textSegment().text(),
                it.metadata()[ContentMetadata.SCORE].toString(),
                it.metadata()[ContentMetadata.RERANKED_SCORE]?.toString(),
                it.textSegment().metadata().getString(Document.FILE_NAME)?: "unknown",
                it.textSegment().metadata().getString("pkm")!!) }
            .toList()
    }
}

@Serializable
data class EmbeddingContent(
    val text: String,
    val score: String,
    val reRank: String?,
    val fileName: String,
    val source: String,
) {
    override fun toString(): String {
        return "EmbeddingContent(text='$text', score=$score)"
    }
}