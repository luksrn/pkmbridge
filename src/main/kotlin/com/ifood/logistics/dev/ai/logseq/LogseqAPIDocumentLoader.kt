package com.ifood.logistics.dev.ai.logseq

import com.ifood.logistics.dev.ai.pkm.PKMDocumentLoader
import dev.langchain4j.data.document.Document
import org.slf4j.LoggerFactory

class LogseqAPIDocumentLoader(val logseqApi: LogseqApi) : PKMDocumentLoader {

    override fun loadDocuments(): List<Document> {
        logger.info("Loading documents from LogSeq API")
        val pages = logseqApi.fetchPages()
        logger.info("${pages.size} pages found in Logseq graph")

        val documents = pages
            .map {
                it.ident = it.inferIdentity()
                it
            }
            .map {
                val blocks = logseqApi.fetchBlocks(it.uuid)
                LogseqDocument(it, blocks.sortedBy { b -> b.order })
            }

        return documents
    }
    private val logger = LoggerFactory.getLogger(LogseqAPIDocumentLoader::class.java)
}