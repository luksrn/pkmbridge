package com.github.luksrn.pkmbridge

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "re-rank")
data class ReRankProperties(
    var enabled: Boolean = false,
    var pathToModel: String? = null,
    var pathToTokenizer: String? = null,
    var maxResult: Int = 20,
    var minScore: Double = 0.0,
)
