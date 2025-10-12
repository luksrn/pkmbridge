package com.github.luksrn.pkmbridge.logseqdb

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class Page(
    var ident: Identity = Identity.PAGE,
    @field:JsonProperty(":logseq.property/public?")
    val public: Boolean = true,
    @field:JsonProperty(":logseq.property/built-in?")
    val builtIn: Boolean = false,
    @field:JsonProperty(":logseq.property/type")
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

@JsonIgnoreProperties(ignoreUnknown = true)
data class Tag(
    val id: Int,
) {
    fun isContent(): Boolean {
        // pages and journal
        return this.id == 134 || this.id == 135
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
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

data class CurrentGraph(
    val url: String,
    val name: String,
    val path: String,
) {
    fun linkForPage(page: Page) = "logseq://graph/${url.replaceFirst("logseq_db_", "")}?page=${page.uuid}&file_name=${page.title.replace(" ", "%20")}"
}

data class LogseqRequest(
    val method: String,
    val args: List<String> = emptyList(),
)

enum class Identity(
    val ident: String,
) {
    PAGE(":logseq.class/Page"),
    JOURNAL(":logseq.class/Journal"),
    TAG(":logseq.class/Tag"),
    PROPERTY(":logseq.class/Property"),
    QUERY(":logseq.class/Query"),
    TASK(":logseq.class/Task"),
    TEMPLATE(":logseq.class/Template"),
    OTHER(""),
    ;

    companion object {
        @JvmStatic
        @JsonCreator
        fun fromIdent(
            @JsonProperty("ident") ident: String,
        ): Identity = Identity.entries.find { it.ident == ident } ?: OTHER
    }
}
