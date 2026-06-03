package com.x.ai.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DebugConfig {
    
    @Value("${spring.ai.dashscope.api-key:NOT_SET}")
    private String apiKey;
    
    @Value("${spring.ai.dashscope.chat.api-key:NOT_SET}")
    private String chatApiKey;
    
    @Bean
    public CommandLineRunner debugRunner() {
        return args -> {
            System.out.println("========================================");
            System.out.println("DEBUG: DashScope Configuration");
            System.out.println("spring.ai.dashscope.api-key: " + apiKey);
            System.out.println("spring.ai.dashscope.chat.api-key: " + chatApiKey);
            System.out.println("========================================");
        };
    }
}
