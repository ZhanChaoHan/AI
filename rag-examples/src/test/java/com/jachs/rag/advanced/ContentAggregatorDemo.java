package com.jachs.rag.advanced;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.ClassPathDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.experimental.rag.content.retriever.sql.SqlDatabaseContentRetriever;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.bgesmallenv15q.BgeSmallEnV15QuantizedEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.aggregator.ContentAggregator;
import dev.langchain4j.rag.content.aggregator.DefaultContentAggregator;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;

/***
ContentAggregator 负责聚合来自以下来源的多个 Content 排名列表：
多个 Query
多个 ContentRetriever
两者兼有
 * @author zhanchaohan
 */
public class ContentAggregatorDemo {
	EmbeddingModel embeddingModel = new BgeSmallEnV15QuantizedEmbeddingModel();
	OpenAiChatModel chatModel =shared.Utils.chatLanguageModel();
	
	@Test
	public void t1() {
		  // 1. 假设这是不同 Query 检索出来的结果（key: 查询, value: 该查询检索到的内容列表）
        Query query1 = Query.from("公司的年假政策是什么？");
        Query query2 = Query.from("员工每年可以休多少天带薪假期？");

        List<Content> resultsForQuery1 = List.of(
                Content.from("员工每年享有5天带薪年假。"),
                Content.from("年假需提前3天申请。")
        );

        List<Content> resultsForQuery2 = List.of(
                Content.from("年假需提前3天申请。"), // 与上面重复，测试去重
                Content.from("未休完的年假可结转至次年。")
        );

        Map<Query, Collection<List<Content>>> queryToContents = Map.of(
                query1, List.of(resultsForQuery1),
                query2, List.of(resultsForQuery2)
        );

        // 2. 创建默认聚合器（内部用 RRF 算法合并排序、并自动去重）
        ContentAggregator aggregator = new DefaultContentAggregator();

        // 3. 执行聚合
        List<Content> aggregatedContents = aggregator.aggregate(queryToContents);

        // 4. 输出聚合后的最终内容（重复的"年假需提前3天申请"只会出现一次）
        for (Content content : aggregatedContents) {
        	System.out.println(content.textSegment().text());
		}
	}
	
	//文档向量化存入pg库
	public List<Content> initDv() {
		 List<Document> documents =Arrays.asList (  
	                ClassPathDocumentLoader.
	                loadDocument("documents/a.txt", new TextDocumentParser()),
	                ClassPathDocumentLoader.
	                loadDocument("documents/b.txt", new TextDocumentParser()));
		 
		 	PgVectorEmbeddingStore embeddingStore =shared.Utils.initPvDb(true);
			
			EmbeddingStoreIngestor.ingest(documents, embeddingStore);//文档写入内存库
			
			
			ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
	                .embeddingStore(embeddingStore)
	                .embeddingModel(embeddingModel)
	                .maxResults(20)
	                .build();//粗排返回20条
			
			List<Content> cList=contentRetriever.retrieve ( Query.from ( "苹果") );
			
			return cList;
	}
	
	//
	public List<Content> initDb() {
		HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3306/langchat?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
        config.setUsername("root");
        config.setPassword("123456");
        
        HikariDataSource dataSource = new HikariDataSource(config);
        
        ContentRetriever contentRetriever = SqlDatabaseContentRetriever.builder()
                .dataSource(dataSource)
                .chatModel(chatModel)
                .build();
		
		List<Content> cList=contentRetriever.retrieve ( Query.from ( "苹果") );
		return cList;
	}
	
	@Test
	public void t2() {
		 ContentAggregator aggregator = new DefaultContentAggregator();
		 Map<Query, Collection<List<Content>>> queryToContents =new HashMap<Query, Collection<List<Content>>>();
		 
		 List<Content>cA= initDv();
		 List<Content>cB=initDb();
		 
		 for (Content content : cA) {
			System.out.print(content);
		 }
		 System.out.println("----------------------------");
		 for (Content content : cB) {
			System.out.print(content);
		 }
		 
		 queryToContents.put(Query.from("苹果"), List.of(cA));
		 queryToContents.put(Query.from("苹果"), List.of(cB));
		 
		 List<Content> aggregatedContents = aggregator.aggregate(queryToContents);

	     // 4. 输出聚合后的最终内容（重复的"年假需提前3天申请"只会出现一次）
	     for (Content content : aggregatedContents) {
	         System.out.println("最后结果:"+content.textSegment().text());
	     }
	        
	}
}
