package com.github.luksrn.pkmbridge.obsidian

import org.springframework.boot.context.properties.ConfigurationProperties
import kotlin.io.path.Path

@ConfigurationProperties(prefix = "pkm.obsidian")
data class ObsidianProperties(
    var enabled: Boolean = true,
    var fileSystemPath: String = "",
) {
    fun getVault() =
        Path(fileSystemPath).fileName.toString()
}
