package com.github.luksrn.pkmbridge.logseqdb

import java.util.UUID
import java.util.function.Function

class LogseqLinkTextResolver {
    companion object {
        private val linkPattern = Regex("\\[\\[([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})\\]\\]")

        fun replaceLinksRecursively(
            block: Block,
            resolver: Function<String, String>,
        ): Block {
            for (child in block.children) {
                replaceLinksRecursively(child, resolver)
            }
            block.content = replaceLinks(block.content, resolver)
            return block
        }

        fun replaceLinks(
            block: Block,
            resolver: Function<String, String>,
        ) {
            replaceLinks(block.content, resolver)?.let { newContent ->
                block.content = newContent
            }
        }

        fun replaceLinks(
            blockContent: String?,
            linkContentResolver: Function<String, String>,
        ): String? {
            if (blockContent.isNullOrEmpty()) {
                return blockContent
            }
            val links = extractLinkedUUIDs(blockContent)
            // If no UUIDs found, return the original document
            if (links.isEmpty()) {
                return blockContent
            }

            val linksAndPages =
                links.map {
                    Pair(it, linkContentResolver.apply(it))
                }

            var transformedText = blockContent!!
            linksAndPages.forEach { (uuid, content) ->
                transformedText = transformedText.replace("[[$uuid]]", content)
            }

            return transformedText
        }

        /**
         * Extracts UUIDs from text that match the pattern [[UUID]]
         */
        private fun extractLinkedUUIDs(text: String): List<String> =
            linkPattern
                .findAll(text)
                .map { it.groupValues[1] } // Extract the UUID from the capture group
                .toList()
    }
}
