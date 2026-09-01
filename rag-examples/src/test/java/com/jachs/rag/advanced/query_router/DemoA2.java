package com.jachs.rag.advanced.query_router;

import java.util.Map;

import org.junit.jupiter.api.Test;

import dev.langchain4j.community.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.data.document.loader.ClassPathDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.bgesmallenv15q.BgeSmallEnV15QuantizedEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.query.router.LanguageModelQueryRouter;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
import shared.Assistant;

/***
进阶案例：LanguageModelQueryRouter（LLM 智能动态路由）
这是生产环境中最常用的方式。它利用大语言模型（LLM）来分析用户意图，并根据每个检索器的描述（Description），动态选择最相关的一个或多个检索器。
‌核心优势：‌
‌精准性‌：避免无关知识库的噪音干扰。
‌灵活性‌：支持单选或多选路由。
‌可解释性‌：可以通过 Prompt 让 LLM 输出路由理由。
‌实施步骤：‌
为每个 ContentRetriever 设置清晰的描述。
配置 LanguageModelQueryRouter，传入 ChatModel。
（可选）自定义 Prompt 模板以规范 LLM 的输出格式。
 * @author zhanchaohan
 */
public class DemoA2 {
	OpenAiChatModel chatModel =shared.Utils.chatLanguageModel();
	
	public PgVectorEmbeddingStore Base(boolean del,String table) {
		EmbeddingModel model = new BgeSmallEnV15QuantizedEmbeddingModel();
		return PgVectorEmbeddingStore.builder()
                .host("localhost")
                .port(5432)
                .database("langchat")
                .user("postgres")
                .password("12345678")
                .table(table)
                .dropTableFirst(del)
                .dimension(model.dimension())  // 384 for AllMiniLmL6V2
                .build();
	}
	
	@Test
	public void initBase() {
		EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .embeddingStore(Base(true,"HR"))
                .build();
		
		 ingestor.ingest(ClassPathDocumentLoader.
	                loadDocument("documents/advanced/a.txt", new TextDocumentParser()));
		
		 EmbeddingStoreIngestor Bingestor = EmbeddingStoreIngestor.builder()
	                .embeddingStore(Base(true,"JS"))
	                .build();
		 
		 Bingestor.ingest(ClassPathDocumentLoader.
	                loadDocument("documents/advanced/b.txt", new TextDocumentParser()));
		
	}
	
	@Test
	public void t2() {
		EmbeddingModel embeddingModel = new BgeSmallEnV15QuantizedEmbeddingModel();
		
		// 1. 定义带有描述信息的检索器
		PgVectorEmbeddingStore hr_embeddingStore =Base(false,"HR");
		PgVectorEmbeddingStore js_embeddingStore =Base(false,"JS");
		
		// 假设已初始化两个不同的检索器
		ContentRetriever jsRetriever = EmbeddingStoreContentRetriever.builder()
		    .embeddingStore(js_embeddingStore)
		    .embeddingModel(embeddingModel)
		    .build();

		ContentRetriever hrRetriever = EmbeddingStoreContentRetriever.builder()
			    .embeddingStore(hr_embeddingStore)
			    .embeddingModel(embeddingModel)
			    .build();
		
		// 2. 构建映射关系
		Map<ContentRetriever,String > retrievers = Map.of(
				hrRetriever,"HR文档", 
				jsRetriever,"技术文档"
		);

		// 3. 创建 LLM 驱动的路由器
		LanguageModelQueryRouter router = LanguageModelQueryRouter.builder()
		    .chatModel(chatModel)
		    .retrieverToDescription(retrievers)
		    // .promptTemplate(...) // 可选：自定义提示词模板
		    .build();

		// 4. 组装增强器
		RetrievalAugmentor augmentor = DefaultRetrievalAugmentor.builder()
		    .queryRouter(router)
		    .build();
		
		ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);//最大保留信息
		
		 
		Assistant assistant=AiServices.builder(Assistant.class)
			        .chatModel(shared.Utils.chatLanguageModel())
			        .retrievalAugmentor(augmentor)
			        .chatMemory(chatMemory)
			        .build();//切片向量数据传递给大模型，建立对话
		 
		shared.Utils.startConversationWith(assistant);
	}
}
