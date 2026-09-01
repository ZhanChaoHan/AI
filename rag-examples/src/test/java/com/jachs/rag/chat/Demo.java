package com.jachs.rag.chat;


import org.junit.jupiter.api.Test;

import dev.langchain4j.model.openai.OpenAiChatModel;


/***
 * @author zhanchaohan
 */
public class Demo {
	OpenAiChatModel chatModel =shared.Utils.chatLanguageModel();
	
    @Test
    public void t1() {
        String response = chatModel.chat ( "请用Java写一个Hello World程序" );
        
        System.out.println ( response);
    }
}
