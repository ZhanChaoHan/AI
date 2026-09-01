package com.jachs.rag.chat.emtion;

import org.junit.jupiter.api.Test;

import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;

/****
 * @author zhanchaohan
 */
public class Demo {
	OpenAiChatModel chatModel =shared.Utils.chatLanguageModel();
	
	
    enum Sentiment {
        POSITIVE, NEUTRAL, NEGATIVE
    }
    
    
    @Test
    public void t1() {
        
        SentimentAnalyzer sentimentAnalyzer = AiServices.create(SentimentAnalyzer.class, chatModel);
        
        Sentiment sentiment = sentimentAnalyzer.analyzeSentimentOf("I love this product!");
        System.out.println(sentiment); // Output: POSITIVE

        boolean positive = sentimentAnalyzer.isPositive("This is a terrible experience.");
        System.out.println(positive); // Output: false
    }
}
