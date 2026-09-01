package com.jachs.rag.chat.chat_memory;

import org.junit.jupiter.api.Test;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

/***
 * @author zhanchaohan
 */
public class MessageWindowChatMemoryDemo {
	String apiKey = System.getenv("deepseek-key");
	
	
	@Test
	public void t1() {
		ChatLanguageModel  model = OpenAiChatModel.builder()
	            .baseUrl("https://api.deepseek.com/v1")
	            .apiKey(apiKey)
	            .modelName("deepseek-chat")
	            .temperature(0.7)
	            .logRequests(true)  // 打印请求日志
	            .logResponses(true) // 打印响应日志
	            .build();
		
		// 2. 创建 MessageWindowChatMemory
        // maxMessages(5) 表示只保留最近的 5 条消息（用户+AI各算一条）
        // 当第 6 条消息加入时，最早的一条非系统消息会被自动移除
        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .maxMessages(5) 
                .build();

        // --- 第一轮对话 ---
        UserMessage msg1 = UserMessage.from("你好，我叫张三。");
        chatMemory.add(msg1); // 将用户消息加入记忆
       
        // 获取当前记忆中的所有消息发送给模型
        AiMessage response1 = model.chat(chatMemory.messages()).aiMessage();
        chatMemory.add(response1); // 将AI回复加入记忆
        
        System.out.println("AI: " + response1.text());

        // --- 第二轮对话 ---
        UserMessage msg2 = UserMessage.from("我叫什么名字？");
        chatMemory.add(msg2);
        
        AiMessage response2 = model.chat(chatMemory.messages()).aiMessage();
        chatMemory.add(response2);
        
        System.out.println("AI: " + response2.text()); 
        // 此时模型能回答“张三”，因为消息在窗口内
        
        // --- 第三轮对话 ---
        UserMessage msg3 = UserMessage.from("我叫什么名字？");
        chatMemory.add(msg3);
        
        AiMessage response3 = model.chat(chatMemory.messages()).aiMessage();
        chatMemory.add(response3);
        
        System.out.println("AI: " + response3.text()); 
	}
}
