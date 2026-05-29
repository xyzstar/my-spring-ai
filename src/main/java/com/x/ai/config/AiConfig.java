package com.x.ai.config;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class AiConfig {

    @Bean
    @Primary
    public ChatClient chatClient(DashScopeChatModel dashScopeChatModel) {
        return ChatClient.builder(dashScopeChatModel).build();
    }
    
    @Bean("ollamaChatClient")
    public ChatClient ollamaChatClient(@Qualifier("ollamaChatModel") ChatModel chatModel) {
        return ChatClient.builder(chatModel).build();
    }
}
