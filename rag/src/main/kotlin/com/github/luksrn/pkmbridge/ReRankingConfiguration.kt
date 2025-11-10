package com.github.luksrn.pkmbridge

import dev.langchain4j.model.scoring.onnx.OnnxScoringModel
import dev.langchain4j.rag.content.aggregator.ContentAggregator
import dev.langchain4j.rag.content.aggregator.ReRankingContentAggregator
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConditionalOnProperty(prefix = "re-rank", name = ["enabled"], havingValue = "true", matchIfMissing = false)
@ConditionalOnProperty(prefix = "re-rank", name = ["path-to-model", "path-to-tokenizer"])
class ReRankingConfiguration {
    @Bean
    fun contentAggregator(reRankProperties: ReRankProperties): ContentAggregator =
        ReRankingContentAggregator
            .builder()
            .querySelector(ReRankingQueryExpandingQuerySelector())
            .scoringModel(OnnxScoringModel(reRankProperties.pathToModel, reRankProperties.pathToTokenizer))
            .maxResults(reRankProperties.maxResult)
            .minScore(reRankProperties.minScore)
            .build()
}
