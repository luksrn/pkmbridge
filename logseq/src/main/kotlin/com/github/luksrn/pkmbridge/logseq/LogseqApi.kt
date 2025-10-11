package com.github.luksrn.pkmbridge.logseq

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.springframework.stereotype.Component

// TODO Refactor
@Component
class LogseqApi(
    private val properties: LogseqProperties,
    private val objectMapper: ObjectMapper
) {
    private val client = OkHttpClient()



    fun fetchPages(): List<Page> {
        val logseqRequest = LogseqRequest("logseq.Editor.getAllPages")
        val jsonString = objectMapper.writeValueAsString(logseqRequest)

        val request =
            Request
                .Builder()
                .url(properties.serverUrl)
                .post(jsonString.toRequestBody("application/json".toMediaTypeOrNull()))
                .header("Authorization", properties.authorizationToken)
                .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw Exception("Failed to fetch pages: ${response.code}")
            val responseBody = response.body?.string() ?: throw Exception("Empty response body")
            return objectMapper.readValue(responseBody, object : TypeReference<MutableList<Page>>() {})
        }
    }

    fun fetchBlocks(pageUuid: String): List<Block> {
        val logseqRequest = LogseqRequest("logseq.Editor.getPageBlocksTree", listOf(pageUuid))
        val jsonString = objectMapper.writeValueAsString(logseqRequest)

        val request =
            Request
                .Builder()
                .url(properties.serverUrl)
                .post(jsonString.toRequestBody("application/json".toMediaTypeOrNull()))
                .header("Authorization", properties.authorizationToken)
                .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw Exception("Failed to fetch blocks: ${response.code}")
            val responseBody = response.body?.string() ?: throw Exception("Empty response body")
            return objectMapper.readValue(responseBody, object : TypeReference<MutableList<Block>>() {})
        }
    }

    fun fetchPage(pageUuid: String): Page {
        val logseqRequest = LogseqRequest("logseq.Editor.getPage", listOf(pageUuid))
        val jsonString = objectMapper.writeValueAsString(logseqRequest)

        val request =
            Request
                .Builder()
                .url(properties.serverUrl)
                .post(jsonString.toRequestBody("application/json".toMediaTypeOrNull()))
                .header("Authorization", properties.authorizationToken)
                .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw Exception("Failed to fetch blocks: ${response.code}")
            val responseBody = response.body?.string() ?: throw Exception("Empty response body")
            return objectMapper.readValue(responseBody, Page::class.java)
        }
    }
}
