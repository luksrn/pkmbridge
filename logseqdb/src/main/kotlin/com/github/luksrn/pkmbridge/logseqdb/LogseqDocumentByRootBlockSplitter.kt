package com.github.luksrn.pkmbridge.logseqdb

import dev.langchain4j.data.document.Document
import dev.langchain4j.data.document.DocumentSplitter
import dev.langchain4j.data.document.Metadata
import dev.langchain4j.data.segment.TextSegment

class LogseqDocumentByRootBlockSplitter : DocumentSplitter {
    override fun split(document: Document?): List<TextSegment> {
        if (document !is LogseqDocument) {
            throw IllegalArgumentException("Document must be a Logseq Document")
        }

        return document.blocks
            .filter { !it.content.isNullOrBlank() }
            .flatMap { block -> createTextSegmentsFromBlock(block, document) }
    }

    private fun createTextSegmentsFromBlock(
        block: Block,
        document: LogseqDocument,
    ): List<TextSegment> {
        val segments = mutableListOf<TextSegment>()

        // Create segment for the current block
        val blockMetadata = buildMetadata(block, document)
        segments.add(TextSegment(block.content!!, blockMetadata))

        // Recursively process children
        addChildSegments(block, document, segments)

        return segments
    }

    private fun buildMetadata(
        block: Block,
        document: LogseqDocument,
    ): Metadata {
        val metadata = Metadata.from("block", block.uuid ?: "unknown")

        metadata.putAll(document.metadata.toMap())

        document.page.journalDay?.let { journalDay ->
            metadata.put("journalDay", journalDay)
        }

        block.level?.let { level ->
            metadata.put("level", level)
        }

        return metadata
    }

    private fun addChildSegments(
        parentBlock: Block,
        document: LogseqDocument,
        segments: MutableList<TextSegment>,
    ) {
        parentBlock.children
            .filter { !it.content.isNullOrBlank() }
            .forEach { childBlock ->
                val childMetadata = buildChildMetadata(childBlock, parentBlock, document)
                segments.add(TextSegment(childBlock.content!!, childMetadata))
                addChildSegments(childBlock, document, segments)
            }
    }

    private fun buildChildMetadata(
        childBlock: Block,
        parentBlock: Block,
        document: LogseqDocument,
    ): Metadata {
        val metadata = buildMetadata(childBlock, document)

        parentBlock.uuid?.let { parentUuid ->
            metadata.put("parentBlock", parentUuid)
        }

        return metadata
    }
}
