package com.github.luksrn.pkmbridge.logseqdb

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "pkm.logseq")
data class LogseqProperties(
    var serverUrl: String = "",
    var authorizationToken: String = "",
)
