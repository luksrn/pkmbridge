package com.ifood.logistics.dev.ai.logseq

import com.ifood.logistics.dev.ai.SummarizerAssistant
import dev.langchain4j.data.document.Document
import dev.langchain4j.data.document.DocumentTransformer

class LogseqDocumentSummarizedTransformer(
    val summarizerAssistant: SummarizerAssistant,
) : DocumentTransformer {
    override fun transform(document: Document): Document? {
        val note = document as LogseqDocument
        val shouldBeIndexed = note.page.tags?.any { it.isContent() } ?: false
        if (!shouldBeIndexed) {
            return null
        }

        if (note.blocks.isEmpty()) {
            return null
        }

        if (note.blocks.all({ it.content.isNullOrBlank() })) {
            return null
        }

        if (note.text().count() < 1000) {
            // Skip summarization for small notes
            return null
        }
        val summary = summarizerAssistant.summarize(note.text()).content()
        document.metadata().put("summary", summary)
        return document
    }
}
