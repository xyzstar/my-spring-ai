package com.x.ai.ai.controller;

import com.x.ai.ai.model.ChatRequest;
import com.x.ai.ai.model.ChatResponse;
import com.x.ai.ai.service.AiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * AI 聊天控制器
 * 提供 AI 对话和模型查询功能
 */
@Slf4j
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final AiService aiService;
    private final RestTemplate restTemplate;

    @Value("${spring.ai.ollama.base-url}")
    private String ollamaBaseUrl;

    public ChatController(AiService aiService) {
        this.aiService = aiService;
        this.restTemplate = new RestTemplate();
    }

    /**
     * 发送聊天消息
     *
     * @param request 聊天请求，包含消息内容和模型名称
     * @return 聊天响应
     */
    @PostMapping
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        try {
            log.info("收到聊天请求，模型: {}, 消息: {}", request.getModel(), request.getMessage());
            String response = aiService.generateResponse(request.getMessage(), request.getModel());
            ChatResponse chatResponse = new ChatResponse(response);
            log.info("聊天请求处理成功");
            return ResponseEntity.ok(chatResponse);
        } catch (Exception e) {
            log.error("聊天请求处理失败", e);
            
            // 判断是否为超时错误
            String errorMessage = e.getMessage();
            if (errorMessage != null && errorMessage.toLowerCase().contains("timeout")) {
                errorMessage = "请求超时，Ollama 服务响应较慢，请稍后重试或尝试其他模型";
            } else if (errorMessage != null && errorMessage.contains("Connection refused")) {
                errorMessage = "无法连接到 Ollama 服务，请检查服务是否启动";
            } else if (errorMessage != null && errorMessage.contains("I/O error")) {
                errorMessage = "网络请求失败，请检查 Ollama 服务是否正常运行";
            } else {
                errorMessage = "处理请求时出错：" + e.getMessage();
            }
            
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ChatResponse(errorMessage));
        }
    }

    /**
     * 获取可用的 AI 模型列表
     *
     * @return 模型名称列表
     */
    @GetMapping("/models")
    public ResponseEntity<java.util.List<String>> getModels() {
        log.info("获取可用模型列表");
        return ResponseEntity.ok(aiService.getAvailableModels());
    }

    /**
     * 获取 Ollama 本地模型列表
     * 调用 Ollama API 获取真实的模型列表
     *
     * @return Ollama 模型名称列表
     */
    @GetMapping("/ollama/models")
    public ResponseEntity<java.util.List<String>> getOllamaModels() {
        try {
            log.info("获取 Ollama 模型列表，地址: {}", ollamaBaseUrl);
            
            // 调用 Ollama API 获取模型列表
            String url = ollamaBaseUrl + "/api/tags";
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            
            if (response != null && response.containsKey("models")) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> models = (List<Map<String, Object>>) response.get("models");
                
                // 提取模型名称
                List<String> modelNames = models.stream()
                        .map(model -> (String) model.get("name"))
                        .collect(Collectors.toList());
                
                log.info("成功获取 {} 个 Ollama 模型", modelNames.size());
                return ResponseEntity.ok(modelNames);
            }
            
            log.warn("Ollama 返回的模型列表为空");
            return ResponseEntity.ok(java.util.Collections.emptyList());
            
        } catch (Exception e) {
            log.error("获取 Ollama 模型列表失败", e);
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(java.util.Collections.singletonList("获取失败: " + e.getMessage()));
        }
    }
}
