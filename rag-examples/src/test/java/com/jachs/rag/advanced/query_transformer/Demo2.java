package com.jachs.rag.advanced.query_transformer;

import org.junit.jupiter.api.Test;

import dev.langchain4j.data.document.loader.ClassPathDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.bgesmallenv15q.BgeSmallEnV15QuantizedEmbeddingModel;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.query.router.DefaultQueryRouter;
import dev.langchain4j.rag.query.router.QueryRouter;
import dev.langchain4j.rag.query.transformer.ExpandingQueryTransformer;
import dev.langchain4j.rag.query.transformer.QueryTransformer;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
import shared.Assistant;

/***
如果你需要将一个问题拆分成多个角度去检索（例如同时检索“优点”和“缺点”），可以实现自定义的 QueryTransformer。
‌场景描述‌：
用户问：“Spring Boot 和 Quarkus 的区别？”
希望拆解为两个查询分别检索：“Spring Boot 的特点” 和 “Quarkus 的特点”，然后合并结果。
 * @author zhanchaohan
 */
public class Demo2 {
	EmbeddingModel model = new BgeSmallEnV15QuantizedEmbeddingModel();//自带模型
	PgVectorEmbeddingStore embeddingStore =shared.Utils.initPvDb(true);
	
	@Test
	public void initDB() {
		EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .embeddingModel(model)
                .embeddingStore(embeddingStore)
                .build();
		
		 ingestor.ingest(ClassPathDocumentLoader.
	                loadDocument("documents/aa.txt", new TextDocumentParser()), 
	                ClassPathDocumentLoader.
	                loadDocument("documents/bb.txt", new TextDocumentParser()));
	}
	
	@Test
	public void t1() {
		PromptTemplate promptTemplate=PromptTemplate.from("每次回答不超过10字");//模板提示
		
		ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);
		OpenAiChatModel chatModel =shared.Utils.chatLanguageModel();
		QueryTransformer expandingTransformer = new ExpandingQueryTransformer(chatModel,promptTemplate);
		

        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(model)
                .minScore(0.0)
                .maxResults(20)
                .build();//全查
        
        RetrievalAugmentor retrievalAugmentor = DefaultRetrievalAugmentor.builder()
                .queryTransformer(expandingTransformer)
                .contentRetriever(contentRetriever)
                .build();
        
        Assistant assistant=AiServices.builder(Assistant.class)
                .chatLanguageModel(chatModel)
                .retrievalAugmentor(retrievalAugmentor)
                .chatMemory(chatMemory)
                .build();
        
        shared.Utils.startConversationWith(assistant);
	}
}
