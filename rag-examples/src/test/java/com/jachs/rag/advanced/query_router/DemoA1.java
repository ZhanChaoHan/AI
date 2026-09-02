package com.jachs.rag.advanced.query_router;



import org.junit.jupiter.api.Test;

import dev.langchain4j.data.document.loader.ClassPathDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.bgesmallenv15q.BgeSmallEnV15QuantizedEmbeddingModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.query.router.DefaultQueryRouter;
import dev.langchain4j.rag.query.router.QueryRouter;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
import shared.Assistant;

/***
在 LangChain4j 中，QueryRouter 是高级 RAG（检索增强生成）架构中的核心组件，主要用于解决“多知识源”场景下的检索分发问题。
它的核心职责是根据用户查询的语义或规则，决定将请求路由到哪一个或多个 ContentRetriever（内容检索器），从而避免全量搜索带来的噪音和性能损耗。
以下是 QueryRouter 的三种典型使用案例，从基础的全量路由到基于 LLM 的智能路由。
1. 基础案例：DefaultQueryRouter（全量/静态路由）
这是最简单的路由实现，适用于数据量较小或需要确保召回率的场景。它会将同一个查询发送给所有注册的检索器，最后由 ContentAggregator合并结果。
‌适用场景：‌
原型开发阶段。
知识库之间界限模糊，或者用户可能同时需要多个库的信息。
对延迟不敏感，追求高召回率。
 * @author zhanchaohan
 */
public class DemoA1 {
	
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
	public void t1() {
		PgVectorEmbeddingStore hr_embeddingStore =Base(false,"HR");
		PgVectorEmbeddingStore js_embeddingStore =Base(false,"JS");
		
		// 假设已初始化两个不同的检索器
		ContentRetriever hrRetriever =EmbeddingStoreContentRetriever.from(hr_embeddingStore);; // HR 知识库
		ContentRetriever techRetriever =EmbeddingStoreContentRetriever.from(js_embeddingStore);; ; // 技术文档库

		// 创建默认路由：将所有查询同时发给 HR 和 Tech 检索器
		QueryRouter router = new DefaultQueryRouter(hrRetriever,techRetriever);

		// 在 RetrievalAugmentor中使用
		RetrievalAugmentor augmentor = DefaultRetrievalAugmentor.builder()
		    .queryRouter(router)
		    .build();
		
		ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);//最大保留信息
		
		 
		 
		Assistant assistant=AiServices.builder(Assistant.class)
			        .chatLanguageModel(shared.Utils.chatLanguageModel())
			        .retrievalAugmentor(augmentor)
			        .chatMemory(chatMemory)
			        .build();//切片向量数据传递给大模型，建立对话
		 
		shared.Utils.startConversationWith(assistant);
	}
}
