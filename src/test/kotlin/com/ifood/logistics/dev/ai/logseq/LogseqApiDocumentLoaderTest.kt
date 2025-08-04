package com.ifood.logistics.dev.ai.logseq

import com.ifood.logistics.dev.ai.pkm.PKMDocumentLoader
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class LogseqApiDocumentLoaderTest {

    var api = LogseqApi(
        LogseqProperties(
            "http://127.0.0.1:12315/api",
            "0432483b-469a-4ca6-b16d-35106294b36e"
        )
    )
    val logseqAPIDocumentLoader: PKMDocumentLoader = LogseqAPIDocumentLoader(
        api
    )

    @Test
    fun `testLoadDocuments`() {
        var loadDocuments = logseqAPIDocumentLoader.loadDocuments()
        Assertions.assertThat(loadDocuments).isNotEmpty

        loadDocuments = LogseqDocumentTransformer(api).transformAll(loadDocuments)
        loadDocuments.forEach { document ->
            println(document.text())
            println(document.text().length)
        }

        var segments = LogseqDocumentByRootBlockSplitter().splitAll(loadDocuments)
        segments.forEach {
            println(it)
        }
    }
}