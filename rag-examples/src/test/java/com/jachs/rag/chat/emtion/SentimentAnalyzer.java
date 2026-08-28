package com.jachs.rag.chat.emtion;

import com.jachs.rag.chat.emtion.Demo.Sentiment;

import dev.langchain4j.service.UserMessage;



/***
 * @author zhanchaohan
 */
public interface  SentimentAnalyzer {
    @UserMessage("Analyze sentiment of {{it}}")
    Sentiment analyzeSentimentOf(String text);

    @UserMessage("Does {{it}} have a positive sentiment?")
    boolean isPositive(String text);
}
