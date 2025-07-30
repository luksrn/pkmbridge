package com.ifood.logistics.dev.ai.logseq

import dev.langchain4j.data.document.Document
import dev.langchain4j.data.document.DocumentTransformer
import dev.langchain4j.data.document.Metadata
import java.util.UUID

@Deprecated("Use LogseqDocumentTransformer instead")
class LogseqLinkDocumentTransformer(val api: LogseqApi) : DocumentTransformer {

    private val linkPattern = Regex("\\[\\[([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})\\]\\]")

    override fun transform(document: Document): Document {

        val source = document as LogseqDocument

        val text = document.text() ?: return document

        val newBlocks = source.blocks
            .map {
                // Extract all UUIDs from the text
                val linkedUUIDs = extractLinkedUUIDs(text)

                // If no UUIDs found, return the original document
                if (linkedUUIDs.isEmpty()) {
                    return@map it
                }

                val page = api.fetchPage(linkedUUIDs.first())
                // todo melhorar recursivo
                return@map Block(
                    uuid = it.uuid,
                    content = it.content!!.replace("[[${linkedUUIDs.first()}]]", page.content!!), // ajustar
                    order = it.order,
                    children = it.children.map { child ->
                        Block(
                            uuid = child.uuid,
                            content = child.content,
                            order = child.order,
                            children = child.children
                        )
                    }
                )
            }

        // Create new metadata with the linked UUIDs
//        val enhancedMetadata = Metadata.from(
//            originalMetadata.toMap() + mapOf("linkedBlocks" to linkedUUIDs.joinToString(","))
//        )

        // Return a new document with the same text but enhanced metadata
        //return Document.from(text, enhancedMetadata)
        val transformedDocument = LogseqDocument(source.page, newBlocks)
        println("Document Origin = ${document.text()}")
        println("Transformed to = ${transformedDocument.text()}")
        return transformedDocument
    }

    private fun extractLinkedUUIDs(block: Block): List<String> {
        return linkPattern.findAll(block.content!!)
            .map { it.groupValues[1] } // Extract the UUID from the capture group
            .toList()
    }
    /**
     * Extracts UUIDs from text that match the pattern [[UUID]]
     */
    private fun extractLinkedUUIDs(text: String): List<String> {
        return linkPattern.findAll(text)
            .map { it.groupValues[1] } // Extract the UUID from the capture group
            .toList()
    }
}