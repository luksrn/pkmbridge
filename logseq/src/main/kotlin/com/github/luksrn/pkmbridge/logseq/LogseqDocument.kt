package com.github.luksrn.pkmbridge.logseq

import dev.langchain4j.data.document.Document
import dev.langchain4j.data.document.Metadata

class LogseqDocument(
    val page: Page,
    val blocks: List<Block>,
    var metadata: Metadata = Metadata(),
) : Document {
    companion object {
        val DOCUMENT_TYPE = "type"
    }

    init {
        metadata.put(Document.FILE_NAME, page.name)
        metadata.put(DOCUMENT_TYPE, page.inferIdentity().name)
        metadata.put("pkm", "logseq")
        metadata.put("link", "logseq://graph/database-teste-03?page=${page.uuid}&file_name=${page.title.replace(" ", "%20")}")
    }

    override fun text(): String {
        var content = "# ${page.title}\n\n"
        content += compileContent(blocks)
        return content
    }

    private fun compileContent(blocks: List<Block>): String =
        blocks.joinToString("") { block ->
            val childContent = block.children.sortedBy { it.order }.let { compileContent(it) }
            "${block.content ?: ""}\n$childContent"
        }

    override fun metadata(): Metadata = metadata

    override fun toString(): String = page.title

    fun doWithBlocksRecursive(action: (Block) -> Unit) {
        doWithBlocksRecursive(blocks, action)
    }

    private fun doWithBlocksRecursive(
        blocks: List<Block>,
        action: (Block) -> Unit,
    ) {
        blocks.forEach { block ->
            action(block)
            doWithBlocksRecursive(block.children, action)
        }
    }
}
