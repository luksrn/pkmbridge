package com.ifood.logistics.dev.ai.logseq

import dev.langchain4j.data.document.Document
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

class LogseqAPIDocumentLoader(val logseqApi: LogseqApi) : LogseqDocumentLoader {

    override fun loadDocuments(): List<Document> {
        val pages = logseqApi.fetchPages()
        logger.info("Logseq graph size ${pages.size}")
        val documents = pages
            .map {
                val blocks = logseqApi.fetchBlocks(it.uuid!!)
                Pair(it, blocks)
            }.map  {
                it.second.sortedBy { it.order }
                LogseqDocument(it.first, it.second)
            }
        logger.info("Logseq graph converted into ${pages.size} documents")
        return documents
    }

    private val logger = LoggerFactory.getLogger(LogseqAPIDocumentLoader::class.java)

}