package com.jachs.langchain4j.chart;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.junit.Test;

import java.time.Duration;

/***
 * @author zhanchaohan
 */
public class Demo {
    String apiKey = System.getenv("deepseek-key");

    //deepseek模型:https://api-docs.deepseek.com/zh-cn/quick_start/pricing

    @Test
    public void t1() {
        ChatLanguageModel mode=OpenAiChatModel.builder()
                .baseUrl("https://api.deepseek.com/v1")
                .apiKey(apiKey)
                .modelName("deepseek-chat")
                .temperature(0.3)
                .maxTokens(2048)
                .timeout(Duration.ofSeconds(60))
                .build();

        String answer = mode.chat("历史上的今天'");
        System.out.println(answer); // Hello World
    }
}
