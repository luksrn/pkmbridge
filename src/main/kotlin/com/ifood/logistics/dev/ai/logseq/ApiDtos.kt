package com.ifood.logistics.dev.ai.logseq

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Page(
    val ident: String? = null,
    @SerialName(":logseq.property/public?")
    val public: Boolean? = null,
    @SerialName(":logseq.property/built-in?")
    val builtIn: Boolean? = null,
    val journalDay: Long? = null,
    val updatedAt: Long? = null,
    val createdAt: Long? = null,
    val tags: List<Tag>? = null,
    val id: Int? = null,
    val name: String? = null,
    val uuid: String? = null,
    val content: String? = null,
    val title: String? = null,
)

@Serializable
data class Tag(
    val id: Int
) {
    fun isContent(): Boolean {
        // pages and journal
        return this.id == 134 || this.id == 135
    }
}

@Serializable
data class Block(
    val children: List<Block> = emptyList(),
    val tags: List<Tag>? = null,
    val id: Int? = null,
    val name: String? = null,
    val uuid: String? = null,
    val content: String? = null,
    val title: String? = null,
    val level: Int? = null,
    val order: String? = null,
)

@Serializable
data class LogseqRequest(
    val method: String,
    val args: List<String> = emptyList()
)