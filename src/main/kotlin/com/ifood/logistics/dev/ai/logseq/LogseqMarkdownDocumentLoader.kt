package com.ifood.logistics.dev.ai.logseq

import dev.langchain4j.data.document.Document
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader
import org.apache.commons.io.filefilter.RegexFileFilter

class LogseqMarkdownDocumentLoader(val directory: String) : PKMDocumentLoader {

    override fun loadDocuments(): List<Document> {
        val markdownOnly = RegexFileFilter("^.*\\.md$") // Regex to match .md files
        val documents = FileSystemDocumentLoader.loadDocumentsRecursively(directory, markdownOnly)
        return documents
    }
}