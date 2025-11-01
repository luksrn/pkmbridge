package com.github.luksrn.pkmbridge

import dev.langchain4j.guardrail.InputGuardrail
import dev.langchain4j.guardrail.OutputGuardrail
import dev.langchain4j.memory.chat.ChatMemoryProvider
import dev.langchain4j.memory.chat.MessageWindowChatMemory
import dev.langchain4j.model.chat.ChatModel
import dev.langchain4j.model.chat.StreamingChatModel
import dev.langchain4j.rag.RetrievalAugmentor
import dev.langchain4j.service.AiServices
import dev.langchain4j.store.memory.chat.InMemoryChatMemoryStore
import org.springframework.beans.factory.ObjectProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class PersonalKnowledgeAssistantConfiguration {
    @Bean
    @Primary
    fun personalKnowledgeAssistant(
        chatModel: ChatModel,
        streamChatModel: StreamingChatModel,
        retrievalAugmentor: RetrievalAugmentor,
        inputGuardrailsProvider: ObjectProvider<InputGuardrail>,
        outputGuardrailsProvider: ObjectProvider<OutputGuardrail>,
    ): PersonalKnowledgeAssistant =
        AiServices
            .builder<PersonalKnowledgeAssistant>(PersonalKnowledgeAssistant::class.java)
            .chatModel(chatModel)
            .streamingChatModel(streamChatModel)
            .chatMemoryProvider(chatMemoryProvider())
            .retrievalAugmentor(retrievalAugmentor)
            .inputGuardrails(inputGuardrailsProvider.orderedStream().toList())
            .outputGuardrails(outputGuardrailsProvider.orderedStream().toList())
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
