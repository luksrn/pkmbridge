package com.github.luksrn.pkmbridge.logseqdb

import dev.langchain4j.data.document.Document
import dev.langchain4j.data.document.DocumentSplitter
import dev.langchain4j.data.document.Metadata
import dev.langchain4j.data.segment.TextSegment

class LogseqDocumentByRootBlockSplitter : DocumentSplitter {
    override fun split(document: Document?): List<TextSegment?>? {
        val logseqDocument = document as LogseqDocument
        val textSegments = createTextSegments(logseqDocument.blocks, null)
        textSegments.forEach { textSegment ->
            textSegment.metadata().putAll(document.metadata.toMap())
            logseqDocument.page.journalDay?.let { journalDay ->
                textSegment.metadata().put("journalDay", journalDay)
            }
        }
        return textSegments
    }

    // This function creates text segments from the blocks in a LogseqDocument.
    // Each block's (and its children)  is wrapped in a TextSegment, and metadata is created from the block's UUID.
    private fun createTextSegments(
        blocks: List<Block>,
        parent: Block?,
    ): List<TextSegment> =
        blocks
            .filter { !it.content.isNullOrBlank() } // Filter out blocks with empty content
            .flatMap { block ->
                val childSegments = block.children.let { createTextSegments(it, block) }
                val metadata = Metadata.from("block", block.uuid!!)
                parent?.let {
                    metadata.put("childrenOf", parent.uuid!!)
                }

                var all = listOf(TextSegment(block.content ?: "", metadata)) + childSegments

                val combinedText = all.joinToString(" ") { it.text() }
                listOf(TextSegment(combinedText, metadata))
            }
}
