package com.github.luksrn.pkmbridge

import dev.langchain4j.memory.chat.ChatMemoryProvider
import dev.langchain4j.memory.chat.MessageWindowChatMemory
import dev.langchain4j.model.chat.ChatModel
import dev.langchain4j.model.chat.StreamingChatModel
import dev.langchain4j.rag.RetrievalAugmentor
import dev.langchain4j.service.AiServices
import dev.langchain4j.store.memory.chat.InMemoryChatMemoryStore
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class AssistantConfiguration {
    @Bean
    @Primary
    fun assistant(
        chatModel: ChatModel,
        streamChatModel: StreamingChatModel,
        retrievalAugmentor: RetrievalAugmentor,
    ): Assistant =
        AiServices
            .builder<Assistant>(Assistant::class.java)
            .chatModel(chatModel)
            .streamingChatModel(streamChatModel)
            .chatMemoryProvider(chatMemoryProvider())
            .retrievalAugmentor(retrievalAugmentor)
            .inputGuardrails(RagOnlyInputGuardrail(), SelfCheckInputGuardrail(chatModel))
            .outputGuardrails(SelfCheckFactsOutputGuardrail())
            // .tools(LogseqApiTool())
            .build()

    @Bean
    fun chatMemoryProvider(): ChatMemoryProvider =
        ChatMemoryProvider { memoryId ->
            MessageWindowChatMemory
                .builder()
                .id(memoryId)
                .maxMessages(10)
                .chatMemoryStore(InMemoryChatMemoryStore())
                .build()
        }
}
