package com.x.ai.ai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AIAssistantService {

    private final ChatClient chatClient;

    @Autowired
    public AIAssistantService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String askQuestion(String question) {
        return chatClient.prompt()
                .system("你是编程导航的助手，擅长回答编程相关问题。")
                .user(question)
                .call()
                .content();
    }
}
