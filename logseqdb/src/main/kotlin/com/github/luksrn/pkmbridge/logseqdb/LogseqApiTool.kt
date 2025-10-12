package com.github.luksrn.pkmbridge.logseqdb

import dev.langchain4j.agent.tool.P
import dev.langchain4j.agent.tool.Tool

class LogseqApiTool {
    @Tool("Provide unique references used to resolve a for a user question")
    fun getReferences(
        @P("All known links supplied by the user") links: List<String>,
    ): Set<String> = links.toSet()
}
