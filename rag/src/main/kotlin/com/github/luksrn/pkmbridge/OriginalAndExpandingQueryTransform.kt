package com.github.luksrn.pkmbridge

import dev.langchain4j.model.chat.ChatModel
import dev.langchain4j.rag.query.Query
import dev.langchain4j.rag.query.transformer.ExpandingQueryTransformer
import dev.langchain4j.rag.query.transformer.QueryTransformer

class OriginalAndExpandingQueryTransform(
    chatModel: ChatModel,
) : QueryTransformer {
    val expandingQueryTransformer = ExpandingQueryTransformer(chatModel)

    override fun transform(query: Query): Collection<Query> {
        val transformedQueries = mutableListOf(query)
        transformedQueries.addAll(expandingQueryTransformer.transform(query))
        return transformedQueries
    }
}
