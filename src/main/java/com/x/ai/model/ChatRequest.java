package com.x.ai.model;

import lombok.Data;

/**
 * 聊天请求模型
 */
@Data
public class ChatRequest {
    /**
     * 用户发送的消息内容
     */
    private String message;
    
    /**
     * 使用的 AI 模型名称
     */
    private String model;
}
