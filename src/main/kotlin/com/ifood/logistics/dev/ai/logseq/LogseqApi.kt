package com.ifood.logistics.dev.ai.logseq

import okhttp3.OkHttpClient
import okhttp3.Request
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.JsonBuilder
import okhttp3.RequestBody
import org.springframework.stereotype.Component
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull


@Component
class LogseqApi(private val properties: LogseqProperties) {

    private val client = OkHttpClient()

    private val json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    fun fetchPages(): List<Page> {

        val logseqRequest = LogseqRequest("logseq.Editor.getAllPages")
        val jsonString = json.encodeToString(LogseqRequest.serializer(), logseqRequest)

        val request = Request.Builder()
            .url("${properties.serverUrl}")
            .post(jsonString.toRequestBody("application/json".toMediaTypeOrNull()))
            .header("Authorization", properties.authorizationToken)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw Exception("Failed to fetch pages: ${response.code}")
            val responseBody = response.body?.string() ?: throw Exception("Empty response body")
            return json.decodeFromString(responseBody)
        }
    }

    fun fetchBlocks(pageUuid: String): List<Block> {
        val logseqRequest = LogseqRequest("logseq.Editor.getPageBlocksTree", listOf(pageUuid))
        val jsonString = json.encodeToString(LogseqRequest.serializer(), logseqRequest)

        val request = Request.Builder()
            .url("${properties.serverUrl}")
            .post(jsonString.toRequestBody("application/json".toMediaTypeOrNull()))
            .header("Authorization", properties.authorizationToken)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw Exception("Failed to fetch blocks: ${response.code}")
            val responseBody = response.body?.string() ?: throw Exception("Empty response body")
            return json.decodeFromString(responseBody)
        }
    }


    fun fetchPage(pageUuid: String): Page {
        val logseqRequest = LogseqRequest("logseq.Editor.getPage", listOf(pageUuid))
        val jsonString = json.encodeToString(LogseqRequest.serializer(), logseqRequest)

        val request = Request.Builder()
            .url("${properties.serverUrl}")
            .post(jsonString.toRequestBody("application/json".toMediaTypeOrNull()))
            .header("Authorization", properties.authorizationToken)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw Exception("Failed to fetch blocks: ${response.code}")
            val responseBody = response.body?.string() ?: throw Exception("Empty response body")
            return json.decodeFromString(responseBody)
        }
    }
}