package com.ifood.logistics.dev.ai.logseq

import dev.langchain4j.data.document.Document

interface LogseqDocumentLoader {
    fun loadDocuments(): List<Document>
}