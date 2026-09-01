package com.jachs.rag.chat;

import org.junit.jupiter.api.Test;

import dev.langchain4j.chain.ConversationalChain;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.store.memory.chat.InMemoryChatMemoryStore;

/***
ChatMessage 的类型
目前有五种类型的聊天消息，每种对应消息的一种来源：
SystemMessage：这是来自系统的消息。 通常，作为开发者，你应该定义此消息的内容。 一般会在这里写明 LLM 在此对话中的角色、 它应如何表现、以何种风格回答等。 LLM 经过训练后会对 SystemMessage 比对其他类型的消息更加关注， 因此要小心，最好不要让终端用户自由定义或向 SystemMessage 注入某些输入。 通常，它位于对话的开头。
‌AiMessage‌: AI 模型返回的消息，包含生成的文本以及可选的工具调用请求（Tool Execution Requests）。
‌ToolExecutionResultMessage‌: 工具执行后的结果消息，用于将外部工具的输出反馈给模型，以便模型进行下一步推理。
UserMessage：来自用户的消息。 用户可以是应用程序的终端用户（人类），也可以是应用程序本身。 它可以包含：
CustomMessage：这是可以包含任意属性的自定义消息。此消息类型只能由 支持它的 ChatModel 实现使用（目前仅限 Ollama）。


contents()：消息的内容。取决于 LLM 支持的模态， 它可以只包含单个文本（String）， 或其他模态。
name()：用户的名称。并非所有模型提供商都支持。
attributes()：附加属性：这些属性不会发送给模型， 但会存储在 ChatMemory 中。


AiMessage：这是 AI 针对已发送消息生成的回复消息。 它可以包含：
text()：文本内容
thinking()：思考/推理内容
toolExecutionRequests()：执行工具的请求。我们将在 另一节中探讨工具。
attributes()：附加属性，通常特定于提供商


ToolExecutionResultMessage：这是 ToolExecutionRequest 的结果。
 * @author zhanchaohan
 * @see https://docs.langchain4j.info/tutorials/chat-and-language-models
 */
public class ChatModelDemo {
	OpenAiChatModel chatModel =shared.Utils.chatLanguageModel();
	
	
    @Test
    public void t1() {
        String answer =chatModel.chat ( "你是谁" );
        System.out.println("回答: " + answer);
    }
    
    
    
    @Test
    public void t2() {
        // 带上下文的多轮对话 (使用 ChatMessage)
        ChatResponse response = chatModel.chat(
            new SystemMessage("你是一个专业的Java助手。"),
            new UserMessage("Hello"),
            new AiMessage("你好！有什么可以帮你的吗？"),
            new UserMessage("如何用 Java 打印 Hello World？")
        );
        
        System.out.println ( response.aiMessage ().text () );
    }
    
    @Test
    public void t3() {
        UserMessage firstUserMessage = UserMessage.from("你好我名字是Jachs");
        AiMessage firstAiMessage = chatModel.chat(firstUserMessage).aiMessage(); 
        UserMessage secondUserMessage = UserMessage.from("你叫什么名字?");
        AiMessage secondAiMessage = chatModel.chat(firstUserMessage, firstAiMessage, secondUserMessage).aiMessage(); 
        
        System.out.println ( secondAiMessage.text () );
       
    }
    
    
    /****
        默认情况下，ChatMemory实现在内存中存储ChatMessage。
        如果需要持久化，可以实现自定义的ChatMemoryStore， 将ChatMessage存储在您选择的任何持久化存储中：
     */
    @Test
    public void t5() {
        InMemoryChatMemoryStore memoryStore=new InMemoryChatMemoryStore ();//本地内存
        
        
//        ChatMemory chatMemory = TokenWindowChatMemory.withMaxTokens(300, new OpenAiTokenCountEstimator(GPT_4_O_MINI));
//        JdbcChatMemoryStore‌ jdbcChatMemoryStore=new JdbcChatMemoryStore‌();
        
//        ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(20);
        
        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .id("12345")
                .maxMessages(10)
                .chatMemoryStore(memoryStore)
                .build();
    }
    
    
    @Test
    public void t6() {
        ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(20);
        //ChatMemory chatMemory = TokenWindowChatMemory.withMaxTokens(300, new OpenAiTokenizer(GPT_3_5_TURBO));

        ConversationalChain chain = ConversationalChain.builder()
                                      .chatModel(chatModel)
                                      .chatMemory(chatMemory)
                                      .build();
        String answer = chain.execute("What are all the movies directed by Quentin Tarantino?");
        System.out.println(answer);

        answer = chain.execute("How old is he?");
        System.out.println(answer);
    }
}
