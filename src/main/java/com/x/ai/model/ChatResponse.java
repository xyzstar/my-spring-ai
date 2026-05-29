package com.x.ai.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 聊天响应模型
 */
@Data
@AllArgsConstructor
public class ChatResponse {
    /**
     * AI 回复的内容
     */
    private String content;
    
    /**
     * 响应时间戳
     */
    private LocalDateTime timestamp;
    
    /**
     * 构造函数，自动生成时间戳
     *
     * @param content AI 回复的内容
     */
    public ChatResponse(String content) {
        this.content = content;
        this.timestamp = LocalDateTime.now();
    }
}
