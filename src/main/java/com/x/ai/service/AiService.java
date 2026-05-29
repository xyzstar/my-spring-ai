package com.x.ai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class AiService {

    private final ChatClient dashScopeChatClient;
    private final ChatClient ollamaChatClient;

    public AiService(
            @Qualifier("chatClient") ChatClient dashScopeChatClient,
            @Qualifier("ollamaChatClient") ChatClient ollamaChatClient) {
        this.dashScopeChatClient = dashScopeChatClient;
        this.ollamaChatClient = ollamaChatClient;
    }

    public String generateResponse(String prompt, String model) {
        // 根据模型名称判断使用哪个客户端
        ChatClient client = isOllamaModel(model) ? ollamaChatClient : dashScopeChatClient;
        
        return client.prompt()
                .system("你是一个 AI 助手")
                .user(prompt)
                .options(org.springframework.ai.chat.prompt.ChatOptions.builder()
                        .model(model)
                        .build())
                .call()
                .content();
    }
    
    private boolean isOllamaModel(String model) {
        // Ollama 模型名称通常包含 ':' 分隔符（如 qwen2.5-coder:7b）
        // 或者可以通过其他方式区分，这里主要依靠 ':' 来判断
        return model != null && model.contains(":");
    }

    public List<String> getAvailableModels() {
        List<String> models = new ArrayList<>();

        // 远程阿里百炼模型列表
        List<String> remoteModels = List.of(
                "deepseek-v4-flash",
                "deepseek-v4-pro",
                "qwen3.7-max-preview",
                "qwen3.7-max-2026-05-20",
                "qwen3.7-max-2026-05-17",
                "qwen3.6-plus",
                "qwen3.6-flash",
                "qwen3.6-35b-a3b",
                "qwen3.6-27b",
                "qwen3.6-max-preview",
                "qwen3.6-flash-2026-04-16",
                "qwen3.5-plus-2026-04-20",
                "qwen3.6-plus-2026-04-02",
                "qwen-flash-character-2026-02-26",
                "kimi-k2.6",
                "glm-5.1",
                "gui-plus-2026-02-26"
        );
        models.addAll(remoteModels);
        return models;
    }

    public void streamResponse(String prompt, SseEmitter emitter) {
        dashScopeChatClient.prompt()
                .user(prompt)
                .stream()
                .content()
                .doOnNext(content -> {
                    try {
                        emitter.send(content);
                    } catch (IOException e) {
                        emitter.completeWithError(e);
                    }
                })
                .doOnComplete(() -> emitter.complete())
                .doOnError(emitter::completeWithError)
                .subscribe();
    }
}
