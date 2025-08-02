package com.ifood.logistics.dev.ai.logseq

import com.vdurmont.emoji.EmojiParser
import dev.langchain4j.data.segment.TextSegment
import dev.langchain4j.data.segment.TextSegmentTransformer

@Deprecated("Use LogseqDocumentTransformer instead")
class LogseqTextSegmentTransformer(val api: LogseqApi) : TextSegmentTransformer {

    private val linkPattern = Regex("\\[\\[([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})\\]\\]")

    override fun transform(segment: TextSegment?): TextSegment? {
        if(segment?.text().isNullOrBlank()){
            return null
        }

        val transformers = listOf(this::transformLinks, this::cleanUpEmojis)

        var finalSegment = segment
        transformers.forEach { transformFunction ->
            finalSegment = transformFunction(finalSegment!!)
            if(finalSegment == null) {
                return null // If any transformation returns null, we stop processing
            }
        }
        return finalSegment
    }

    private fun transformLinks(segment: TextSegment) : TextSegment? {

        if(segment.text().isNullOrBlank()) {
            return segment
        }

        var patterns = linkPattern.findAll(segment.text()!!)
        if(patterns.any()) {
            val linksAndPages = patterns
                .map { it.groupValues[1] } // Extract the UUID from the capture group
                .filterNot { segment.text().equals("[[$it]]") } // Ensure the UUID is not a standalone link
                .map {
                    val page = api.fetchPage(it)
                    Pair(it, page)
                }.toList()

            if(linksAndPages.isEmpty()) {
                return null
            }

            var transformedText = segment.text()
            linksAndPages.forEach { (uuid, page) ->
                transformedText = transformedText.replace("[[$uuid]]", page.title ?: "")
            }
            return TextSegment(transformedText, segment.metadata())
        }
        return segment
    }

    private fun cleanUpEmojis(textSegment: TextSegment) : TextSegment? {
        return TextSegment(EmojiParser.removeAllEmojis(textSegment.text()), textSegment.metadata())
    }
}