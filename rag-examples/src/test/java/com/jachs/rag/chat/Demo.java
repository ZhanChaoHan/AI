package com.jachs.rag.chat;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

/***
 * @author zhanchaohan
 */
public class Demo {
    String apiKey = System.getenv("deepseek-key");
    
    @Test
    public void t1() {
        ChatLanguageModel  chatModel=(ChatLanguageModel) OpenAiChatModel.builder()
        .apiKey(apiKey)
        .baseUrl("https://api.deepseek.com/v1") // DeepSeek官方API端点
        .modelName("deepseek-chat") // 可选deepseek-reasoner（推理模型）
        .temperature(1.3) // DeepSeek推荐>1.0以获得更好生成效果
        .timeout(Duration.ofSeconds(60))
        .maxTokens(1000)
        .build();//封装对话大模型对象
        
        
        String response = chatModel.chat ( "请用Java写一个Hello World程序" );
        
        System.out.println ( response);
    }
}
