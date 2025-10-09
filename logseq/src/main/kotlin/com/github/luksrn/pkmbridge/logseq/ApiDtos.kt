package com.github.luksrn.pkmbridge.logseq

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Page(
    var ident: Identity = Identity.PAGE,
    @SerialName(":logseq.property/public?")
    val public: Boolean = true,
    @SerialName(":logseq.property/built-in?")
    val builtIn: Boolean = false,
    @SerialName(":logseq.property/type")
    val type: String? = null,
    val journalDay: Long? = null,
    val updatedAt: Long,
    val createdAt: Long,
    val tags: List<Tag>? = null,
    val refs: List<Tag>? = null,
    val id: Int,
    val name: String,
    val uuid: String,
    var content: String? = null,
    val title: String,
) {
    fun inferIdentity(): Identity =
        when {
            this.journalDay != null -> Identity.JOURNAL
            this.ident != Identity.OTHER -> this.ident
            this.tags?.any { it.isContent() } == true -> Identity.PAGE
            else -> Identity.PAGE
        }
}

@Serializable
data class Tag(
    val id: Int,
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
    var content: String? = null,
    val title: String? = null,
    val level: Int? = null,
    val order: String? = null,
)

@Serializable
data class LogseqRequest(
    val method: String,
    val args: List<String> = emptyList(),
)

@Serializable
enum class Identity(
    val ident: String,
) {
    @SerialName(":logseq.class/Page")
    PAGE(":logseq.class/Page"),

    @SerialName(":logseq.class/Journal")
    JOURNAL(":logseq.class/Journal"),

    @SerialName(":logseq.class/Tag")
    TAG(":logseq.class/Tag"),

    @SerialName(":logseq.class/Property")
    PROPERTY(":logseq.class/Property"),

    @SerialName(":logseq.class/Query")
    QUERY(":logseq.class/Query"),

    @SerialName(":logseq.class/Task")
    TASK(":logseq.class/Task"),

    @SerialName(":logseq.class/Template")
    TEMPLATE(":logseq.class/Template"),
    OTHER(""),
    ;

    companion object {
        fun valueOfIdent(ident: String): Identity = values().find { it.ident == ident } ?: OTHER
    }
}
