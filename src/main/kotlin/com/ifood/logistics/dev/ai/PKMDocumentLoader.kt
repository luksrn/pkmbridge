package com.ifood.logistics.dev.ai

import dev.langchain4j.data.document.Document

interface PKMDocumentLoader {
    fun loadDocuments(): List<Document>
}
