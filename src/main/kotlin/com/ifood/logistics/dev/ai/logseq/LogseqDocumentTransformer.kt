package com.ifood.logistics.dev.ai.logseq

import dev.langchain4j.data.document.Document
import dev.langchain4j.data.document.DocumentTransformer

class LogseqDocumentTransformer : DocumentTransformer {

    override fun transform(document: Document): Document? {
        val note = document as LogseqDocument
        val shouldBeIndexed = note.page.tags?.any { it.isContent() } ?: false
        if (!shouldBeIndexed) {
            return null
        }

        if(note.blocks.isEmpty()){
            return null
        }

        if(note.blocks.all({ it.content.isNullOrBlank() })) {
            return null
        }
        return document
    }
}