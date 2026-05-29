package com.x.ai.ai.service;

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
        
        // 添加阿里百炼模型列表
        models.add("qwen-plus");
        models.add("qwen-turbo");
        models.add("qwen-max");
        models.add("qwen-max-longcontext");
        models.add("qwen-vl-plus");
        models.add("qwen-vl-max");
        models.add("qwen-audio-turbo");
        models.add("deepseek-v3");
        models.add("deepseek-r1");
        models.add("glm-4-plus");
        models.add("glm-4-flash");
        
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
