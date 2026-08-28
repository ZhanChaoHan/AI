package com.jachs.rag.chat.emtion;

import org.junit.jupiter.api.Test;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;

/****
 * @author zhanchaohan
 */
public class Demo {
    String apiKey = System.getenv("deepseek-key");
    
    enum Sentiment {
        POSITIVE, NEUTRAL, NEGATIVE
    }
    
    
    @Test
    public void t1() {
        ChatLanguageModel model = OpenAiChatModel.builder()
                .baseUrl("https://api.deepseek.com/v1")
                .apiKey(apiKey)
                .modelName("deepseek-chat")
                .temperature(0.7)
                .logRequests(true)  // 打印请求日志
                .logResponses(true) // 打印响应日志
                .build();
        
        SentimentAnalyzer sentimentAnalyzer = AiServices.create(SentimentAnalyzer.class, model);
        
        Sentiment sentiment = sentimentAnalyzer.analyzeSentimentOf("I love this product!");
        System.out.println(sentiment); // Output: POSITIVE

        boolean positive = sentimentAnalyzer.isPositive("This is a terrible experience.");
        System.out.println(positive); // Output: false
    }
}
