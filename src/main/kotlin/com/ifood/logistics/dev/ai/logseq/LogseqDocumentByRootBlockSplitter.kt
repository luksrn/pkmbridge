package com.ifood.logistics.dev.ai.logseq

import dev.langchain4j.data.document.Document
import dev.langchain4j.data.document.DocumentSplitter
import dev.langchain4j.data.document.Metadata
import dev.langchain4j.data.segment.TextSegment

class LogseqDocumentByRootBlockSplitter : DocumentSplitter{

    override fun split(document: Document?): List<TextSegment?>? {
        if(document is LogseqDocument) {
            val logseqDocument = document as LogseqDocument
            val textSegments = createTextSegments(logseqDocument.blocks, null)
            println("LogseqDocumentSplitter: Splitting document with ${textSegments.size} text segments")
            textSegments.forEach {
                it.metadata().put("source", logseqDocument.page.title)
                logseqDocument.page.journalDay?.let { journalDay ->
                    it.metadata().put("journalDay", journalDay)
                }
            }
            return textSegments
        }
        throw RuntimeException("Document is not an instance of LogseqDocument")
    }
    // This function creates text segments from the blocks in a LogseqDocument.
    // Each block's (and its children)  is wrapped in a TextSegment, and metadata is created from the block's UUID.
    // TODO add tags into metadata
    private fun createTextSegments(blocks: List<Block>, parent: Block?): List<TextSegment> {
        return blocks
            .filter { !it.content.isNullOrBlank() } // Filter out blocks with empty content
            .flatMap { block ->
            val childSegments = block.children?.let { createTextSegments(it, block) } ?: emptyList()
            val metadata = Metadata.from("block", block.uuid!!)
            parent?.let {
                metadata.put("childrenOf", parent.uuid!!)
            }

            var all = listOf(TextSegment(block.content ?: "", metadata)) + childSegments

            val combinedText = all.joinToString("\n") { it.text() }
             listOf(TextSegment(combinedText, metadata))
        }
    }
}