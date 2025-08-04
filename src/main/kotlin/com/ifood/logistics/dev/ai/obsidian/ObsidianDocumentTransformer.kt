package com.ifood.logistics.dev.ai.obsidian

import dev.langchain4j.data.document.Document
import dev.langchain4j.data.document.DocumentTransformer

class ObsidianDocumentTransformer : DocumentTransformer {

    override fun transform(document: Document): Document? {
        // Obsidian documents are already in the correct format, so we can return them as is.
        // Additional transformations can be added here if needed.

        document.metadata().put("pkm", "obsidian")
        return document
    }
}