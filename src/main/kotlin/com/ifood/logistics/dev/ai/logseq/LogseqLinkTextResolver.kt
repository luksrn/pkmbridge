package com.ifood.logistics.dev.ai.logseq

import java.util.UUID
import java.util.function.Function

class LogseqLinkTextResolver {

    companion object {
        private val linkPattern = Regex("\\[\\[([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})\\]\\]")

        fun replaceLinks(block: Block,resolver: Function<String, String>): Block {
            block.content = replaceLinks(block.content, resolver)
            return block
        }

        fun replaceLinks(text: String?, resolver: Function<String, String>): String? {
            if (text.isNullOrEmpty()) {
                return text
            }
            val links = extractLinkedUUIDs(text)
            // If no UUIDs found, return the original document
            if (links.isEmpty()) {
                return text
            }

            val linksAndPages = links.map {
                Pair(it,  resolver.apply(it))
            }

            var transformedText = text!!
            linksAndPages.forEach { (uuid, name) ->
                transformedText = transformedText.replace("[[$uuid]]", name ?: "")
            }

            return transformedText
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
}