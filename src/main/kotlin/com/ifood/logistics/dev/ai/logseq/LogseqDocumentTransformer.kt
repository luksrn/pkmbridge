package com.ifood.logistics.dev.ai.logseq

import dev.langchain4j.data.document.Document
import dev.langchain4j.data.document.DocumentTransformer

class LogseqDocumentTransformer(val api: LogseqApi) : DocumentTransformer {

    override fun transform(document: Document): Document? {
        val note = document as LogseqDocument
        val isPublic = note.page.public?: false // Ensure the page is public for indexing
        if(!isPublic){
            return null
        }
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

        note.blocks.map { block ->
            LogseqLinkTextResolver.replaceLinks(block) { pageId ->
                api.fetchPage(pageId).title
            }
        }
        return document
    }
}