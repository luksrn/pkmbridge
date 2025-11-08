package com.github.luksrn.pkmbridge

import com.fasterxml.jackson.annotation.JsonProperty

data class GenerateRequestDto(
    val model: String,
    val prompt: String,
    val stream: Boolean = false,
    val format: String? = null,
)

data class Options(
    val temperature: Double,
    @field:JsonProperty("top_k")
    val topK: Int,
    @field:JsonProperty("top_p")
    val topP: Double,
    val stop: List<String> = emptyList(),
)

data class ChatRequestDto(
    val model: String,
    val messages: List<Message>,
    val options: Options? = null,
    val stream: Boolean = false,
    val format: String? = null,
)

data class Message(
    val role: String,
    val content: String,
)
