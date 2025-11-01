package com.github.luksrn.pkmbridge.logseqdb

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.restclient.test.autoconfigure.RestClientTest
import org.springframework.core.io.ClassPathResource
import org.springframework.http.MediaType
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.header
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess

@RestClientTest(LogseqRestClient::class, LogseqProperties::class, LogseqRestClientConfig::class)
class LogseqRestClientTest(
    @Autowired val server: MockRestServiceServer,
    @Autowired val client: LogseqRestClient,
    @Autowired val properties: LogseqProperties,
) {
    @Test
    fun `When call the method get current graph the Logseq Client must return Graph info`() {
        server
            .expect(requestTo(properties.serverUrl))
            .andExpect(header("Authorization", properties.authorizationToken))
            .andRespond(
                withSuccess(
                    ClassPathResource("mock-responses-http/getCurrentGraph.json"),
                    MediaType.APPLICATION_JSON,
                ),
            )

        val graph = client.getCurrentGraph()

        val softly = SoftAssertions()
        softly.assertThat(graph.name).isEqualTo("logseq_db_database-teste-04")
        softly.assertThat(graph.url).isEqualTo("logseq_db_database-teste-04")
        softly.assertThat(graph.path).isEqualTo("/Users/luksrn/logseq/graphs/database-teste-04")

        softly.assertAll()
        server.verify()
    }

    @Test
    fun `When call the method fetch pages the Logseq Clint must return all Pages`() {
        server
            .expect(requestTo(properties.serverUrl))
            .andExpect(header("Authorization", properties.authorizationToken))
            .andRespond(
                withSuccess(
                    ClassPathResource("mock-responses-http/getAllPages.json"),
                    MediaType.APPLICATION_JSON,
                ),
            )

        val pages = client.fetchPages()

        assertThat(pages).hasSize(44)
        // TODO improve
        server.verify()
    }

    @Test
    fun `When call the method fetch pages blocks the Logseq Clint must return the block tree`() {
        server
            .expect(requestTo(properties.serverUrl))
            .andExpect(header("Authorization", properties.authorizationToken))
            .andRespond(
                withSuccess(
                    ClassPathResource("mock-responses-http/getPageBlocksTree.json"),
                    MediaType.APPLICATION_JSON,
                ),
            )

        val pages = client.fetchBlocks("68ea7c77-7e14-489f-88ae-8ea9a40b0a77")

        assertThat(pages)
            .hasSize(2)
        // TODO improve
        server.verify()
    }

    @Test
    fun `When call the method fetch page must return the block tree`() {
        server
            .expect(requestTo(properties.serverUrl))
            .andExpect(header("Authorization", properties.authorizationToken))
            .andRespond(
                withSuccess(
                    ClassPathResource("mock-responses-http/getPage.json"),
                    MediaType.APPLICATION_JSON,
                ),
            )

        val page = client.fetchPage("68ea7c77-7e14-489f-88ae-8ea9a40b0a77")

        assertThat(page.title).isEqualTo("Best Practices for Dependencies")
        assertThat(page.content).isEqualTo("Best Practices for Dependencies")
        // TODO improve
        server.verify()
    }
}
