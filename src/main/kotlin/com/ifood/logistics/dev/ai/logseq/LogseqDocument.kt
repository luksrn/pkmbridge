package com.ifood.logistics.dev.ai.logseq

import dev.langchain4j.data.document.Document
import dev.langchain4j.data.document.Metadata

class LogseqDocument(
    val page: Page,
    val blocks: List<Block>,
    var metadata: Metadata = Metadata()
) : Document {

    init {
        metadata.put(Document.FILE_NAME, page.name)
        metadata.put("type", page.inferIdentity().name)
    }

    override fun text(): String {
        var content = "# ${page.title}\n\n"
        content += compileContent(blocks)
        return content
    }

    private fun compileContent(blocks: List<Block>): String {
        return blocks.joinToString("") { block ->
            val childContent = block.children.sortedBy { it.order }?.let { compileContent(it) } ?: ""
            "${block.content ?: ""}\n$childContent"
        }
    }

    override fun metadata(): Metadata {
        return metadata
    }

    override fun toString(): String {
        return page.title
    }
}