package com.ifood.logistics.dev.ai.obsidian

import com.ifood.logistics.dev.ai.PKMDocumentLoader
import dev.langchain4j.data.document.Document
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader
import org.apache.commons.io.filefilter.RegexFileFilter

class ObsidianMarkdownDocumentLoader(
    val directory: String,
) : PKMDocumentLoader {
    override fun loadDocuments(): List<Document> =
        FileSystemDocumentLoader.loadDocumentsRecursively(directory, RegexFileFilter("^.*\\.md$"))
}
