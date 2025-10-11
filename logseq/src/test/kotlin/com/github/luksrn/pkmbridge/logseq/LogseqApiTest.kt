package com.github.luksrn.pkmbridge.logseq

import org.junit.jupiter.api.Test

class LogseqApiTest {
    val api =
        LogseqApi(
            LogseqProperties(
                "http://127.0.0.1:12315/api",
                "0432483b-469a-4ca6-b16d-35106294b36e",
            ),
        )

    @Test
    fun `Fetch All Pages`() {
        val pages = api.fetchPages()
        pages.forEach {
            println(it)
            api.fetchPage(it.uuid).let { page ->
                println("  $page")
            }
            api.fetchBlocks(it.uuid).forEach {
                println("    $it")
            }
        }
    }
}
