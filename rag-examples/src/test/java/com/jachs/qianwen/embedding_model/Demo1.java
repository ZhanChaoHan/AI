package com.jachs.qianwen.embedding_model;

import org.junit.jupiter.api.Test;

import dev.langchain4j.community.model.dashscope.QwenEmbeddingModel;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;

/***
 * @author zhanchaohan
 */
public class Demo1 {
	String apiKey ="Bearer "+ System.getenv("bainian_02");
	int dimension=1024;
	
	@Test
	public void t1() {
		PgVectorEmbeddingStore store=shared.Utils.initPvDb2(true,dimension,"qianwen");
		
		EmbeddingModel embeddingModel = QwenEmbeddingModel.builder()
                .apiKey(apiKey) 
                .modelName("text-embedding-v3")
                .build();
		
		String text = "LangChain4j 是一个用于 Java 的大语言模型应用开发框架。";
        TextSegment segment = TextSegment.from(text);
        
        try {
        	store.add(embeddingModel.embed(segment).content(),TextSegment.from(text));
        	
        } catch (Exception e) {
            e.printStackTrace();
        }
        
	}
}
