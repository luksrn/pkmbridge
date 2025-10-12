package com.github.luksrn.pkmbridge.logseqdb

import dev.langchain4j.data.document.Document
import dev.langchain4j.data.document.DocumentTransformer

class LogseqDocumentTransformer(
    val client: LogseqRestClient,
) : DocumentTransformer {
    override fun transform(document: Document): Document? {
        val note = document as LogseqDocument

        if (!note.page.public) {
            return null
        }

        val shouldBeIndexed =
            setOf(
                Identity.PAGE.name,
                Identity.JOURNAL.name,
            ).contains(note.metadata.getString(LogseqDocument.DOCUMENT_TYPE))
        if (!shouldBeIndexed) {
            return null
        }

        if (note.blocks.isEmpty()) {
            return null
        }

        if (note.blocks.all({ it.content.isNullOrBlank() })) {
            return null
        }

        note.doWithBlocksRecursive { block ->
            LogseqLinkTextResolver.replaceLinks(block) { pageId ->
                client.fetchPage(pageId).title
            }
        }

        return document
    }
}
