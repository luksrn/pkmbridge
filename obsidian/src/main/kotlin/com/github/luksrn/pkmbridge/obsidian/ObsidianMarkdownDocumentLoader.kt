package com.github.luksrn.pkmbridge.obsidian

import dev.langchain4j.data.document.Document
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader
import org.apache.commons.io.filefilter.RegexFileFilter

class ObsidianMarkdownDocumentLoader(
    val directory: String,
)  {
    fun loadDocuments(): List<Document> =
        FileSystemDocumentLoader.loadDocumentsRecursively(directory, RegexFileFilter("^.*\\.md$"))
}
