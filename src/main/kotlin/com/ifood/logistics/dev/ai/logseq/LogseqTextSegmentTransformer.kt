package com.ifood.logistics.dev.ai.logseq

import dev.langchain4j.data.segment.TextSegment
import dev.langchain4j.data.segment.TextSegmentTransformer

class LogseqTextSegmentTransformer(val api: LogseqApi) : TextSegmentTransformer {

    private val linkPattern = Regex("\\[\\[([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})\\]\\]")

    override fun transform(segment: TextSegment): TextSegment {

        val linksAndPages = linkPattern.findAll(segment.text()!!)
            .map { it.groupValues[1] } // Extract the UUID from the capture group
            .map {
                val page = api.fetchPage(it)
                Pair(it, page)
            }.toList()

        if (linksAndPages.isEmpty()) {
            return segment
        }
        segment
            .text()
            ?.let { text ->

            }
        var transformedText = segment.text()
        linksAndPages.forEach { (uuid, page) ->
            transformedText = transformedText.replace("[[$uuid]]", page.title ?: "")
        }
        return TextSegment(transformedText, segment.metadata())
    }
}