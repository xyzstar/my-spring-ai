package com.x.ai.ai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class ProgrammingAssistantService {
    private final ChatClient chatClient;

    public ProgrammingAssistantService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String getCodeExample(String language, String task) {
        String systemMessage = String.format(
                "你是一位 %s 编程专家，请提供简洁但完整的代码示例。遵循最佳实践，并添加适量注释解释关键逻辑。",
                language
        );

        String userMessage = String.format("请用 %s 实现以下功能：%s", language, task);

        return chatClient.prompt()
                .system(systemMessage)
                .user(userMessage)
                .call()
                .content();
    }
}
