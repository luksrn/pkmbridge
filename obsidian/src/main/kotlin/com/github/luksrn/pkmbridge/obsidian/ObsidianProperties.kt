package com.github.luksrn.pkmbridge.obsidian

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "pkm.obsidian")
data class ObsidianProperties(
    var enabled: Boolean = false,
    var fileSystemPath: String = "",
)
