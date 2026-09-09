package com.jachs.rag.advanced.query_transformer;

import org.junit.jupiter.api.Test;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.query.transformer.CompressingQueryTransformer;
import dev.langchain4j.rag.query.transformer.QueryTransformer;
import dev.langchain4j.service.AiServices;
import shared.Assistant;
import shared.Utils;

/**
使用 CompressingQueryTransformer 处理多轮对话
这是最常用的场景。当用户进行多轮对话时，直接搜索最新的一句话往往无法命中目标。
CompressingQueryTransformer 利用大模型理解上下文，生成一个包含完整语义的新查询。
 * @author zhanchaohan
 */
public class Demo1 {

	@Test
	public void t1() {
		// 假设已经初始化了 chatModel 和 contentRetriever
		OpenAiChatModel chatModel=Utils.chatLanguageModel();
		ContentRetriever contentRetriever = null;

		// 1. 创建压缩查询转换器
		// 它需要传入一个 ChatLanguageModel 来理解上下文并重写查询
		QueryTransformer queryTransformer = new CompressingQueryTransformer(chatModel);

		// 2. 构建检索增强器 (Retrieval Augmentor)
		DefaultRetrievalAugmentor retrievalAugmentor = DefaultRetrievalAugmentor.builder()
		        .queryTransformer(queryTransformer) // 设置转换器
		        .contentRetriever(contentRetriever) // 设置检索器
		        .build();

		// 3. 创建 AI 服务
		Assistant assistant= AiServices.builder(Assistant.class).chatMemory ( MessageWindowChatMemory.withMaxMessages(10) )
    	        .contentRetriever ( contentRetriever )
    	        .chatLanguageModel(chatModel)
    	        .build ();

		// 4. 执行对话
		// 用户输入: "那 Milvus 怎么集成?"
		// 内部流程: Transformer 将其结合上文改为 "LangChain4j 如何集成 Milvus?" -> 检索 -> 生成回答
		String answer = assistant.answer("那 Milvus 怎么集成?");
		
	}
}
