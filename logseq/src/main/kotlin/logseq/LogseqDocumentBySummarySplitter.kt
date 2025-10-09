package com.ifood.logistics.dev.ai.logseq

import dev.langchain4j.data.document.Document
import dev.langchain4j.data.document.DocumentSplitter
import dev.langchain4j.data.document.Metadata
import dev.langchain4j.data.segment.TextSegment

class LogseqDocumentBySummarySplitter : DocumentSplitter {
    override fun split(document: Document): List<TextSegment> {
        val summary = document.metadata().getString("summary") ?: return listOf()
        return listOf(TextSegment(summary, document.metadata()))
    }
}
