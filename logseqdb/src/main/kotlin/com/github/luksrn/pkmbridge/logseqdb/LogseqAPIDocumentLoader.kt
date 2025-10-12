package com.github.luksrn.pkmbridge.logseqdb

import dev.langchain4j.data.document.Document
import dev.langchain4j.data.document.Metadata
import org.slf4j.LoggerFactory

class LogseqAPIDocumentLoader(
    val logseqRestClient: LogseqRestClient,
) {
    fun loadDocuments(): List<Document> {
        val graph = logseqRestClient.getCurrentGraph()
        logger.info("Loading documents from LogSeq API on graph: ${graph.path}")
        val pages = logseqRestClient.fetchPages()
        logger.info("${pages.size} pages found in Logseq graph")

        val documents =
            pages
                .map {
                    it.ident = it.inferIdentity()
                    it
                }.map {
                    val blocks = logseqRestClient.fetchBlocks(it.uuid)
                    val metadata = Metadata()
                    metadata.put("graph-name", graph.name)
                    metadata.put("graph-path", graph.path)
                    LogseqDocument(it, blocks.sortedBy { b -> b.order }, metadata)
                }

        return documents
    }

    private val logger = LoggerFactory.getLogger(LogseqAPIDocumentLoader::class.java)
}
