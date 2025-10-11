package com.github.luksrn.pkmbridge.logseq

import okhttp3.internal.toImmutableList
import org.springframework.core.ParameterizedTypeReference
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class LogseqRestClient(
    properties: LogseqProperties,
    restClientBuilder: RestClient.Builder,
) {
    val restClient =
        restClientBuilder
            .baseUrl(properties.serverUrl)
            .defaultHeader("Authorization", properties.authorizationToken)
            .build()

    fun fetchPages(): List<Page> =
        post(LogseqRequest("logseq.Editor.getAllPages"))
            .toEntity(object : ParameterizedTypeReference<MutableList<Page>>() {})
            .body!!.toImmutableList()

    fun fetchBlocks(pageUuid: String): List<Block> =
        post(LogseqRequest("logseq.Editor.getPageBlocksTree", listOf(pageUuid)))
            .toEntity(object : ParameterizedTypeReference<MutableList<Block>>() {})
            .body!!.toImmutableList()

    fun fetchPage(pageUuid: String): Page =
        post(LogseqRequest("logseq.Editor.getPage", listOf(pageUuid)))
            .toEntity(Page::class.java)
            .body!!

    private fun post(request: Any): RestClient.ResponseSpec =
        restClient
            .post()
            .body(request)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
}
