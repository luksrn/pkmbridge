package com.github.luksrn.pkmbridge

data class GenerateRequestDto(
    val model: String,
    val prompt: String,
    val stream: Boolean = false,
    val format: String? = null,
)

data class ChatRequestDto(
    val model: String,
    val messages: List<Message>,
    val stream: Boolean = false,
    val format: String? = null,
)

data class Message(
    val role: String,
    val content: String,
)
