package com.github.luksrn.pkmbridge

data class GenerateRequestDto(
    val model: String,
    val prompt: String,
    val stream: Boolean = false,
    val format: String? = null,
)

data class Options(
    val temperature: Double,
    val top_k: Int,
    val top_p: Double,
    val stop: List<String> = emptyList(),
)

data class ChatRequestDto(
    val model: String,
    val messages: List<Message>,
    val options: Options? = null,
    val stream: Boolean = false,
    // val tools: List<String> = emptyList()
    val format: String? = null,
)

data class Message(
    val role: String,
    val content: String,
)
// https://ollama.com/blog/structured-outputs
