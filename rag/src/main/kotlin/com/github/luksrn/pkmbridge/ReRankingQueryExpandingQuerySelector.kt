package com.github.luksrn.pkmbridge

import dev.langchain4j.rag.content.Content
import dev.langchain4j.rag.query.Query
import java.util.function.Function
import kotlin.collections.first
import kotlin.collections.iterator

class ReRankingQueryExpandingQuerySelector : Function<Map<Query, Collection<List<Content>>>, Query> {
    override fun apply(queryToContents: Map<Query, Collection<List<Content>>>): Query {
        // select the query that retrieved the largest number of contents
        var selected = queryToContents.keys.first()
        for (query in queryToContents) {
            if (query.value.first().size > queryToContents[selected]!!.first().size) {
                selected = query.key
            }
        }
        return selected
    }
}
