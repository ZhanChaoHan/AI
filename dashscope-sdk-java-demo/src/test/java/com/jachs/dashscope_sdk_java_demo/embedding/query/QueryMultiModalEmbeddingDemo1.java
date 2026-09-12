package com.jachs.dashscope_sdk_java_demo.embedding.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.alibaba.dashscope.embeddings.MultiModalEmbedding;
import com.alibaba.dashscope.embeddings.MultiModalEmbeddingItemImage;
import com.alibaba.dashscope.embeddings.MultiModalEmbeddingItemText;
import com.alibaba.dashscope.embeddings.MultiModalEmbeddingParam;
import com.alibaba.dashscope.embeddings.MultiModalEmbeddingResult;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.exception.UploadFileException;
import com.jachs.dashscope_sdk_java_demo.util.Utils;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;

/***
 * @author zhanchaohan
 */
public class QueryMultiModalEmbeddingDemo1 {
	String apiKey = "Bearer " + System.getenv("bainian_02");
	String modelName = "qwen2.5-vl-embedding";
	boolean delBase=false;//是否删库
	int dimension =1024;
	
	PgVectorEmbeddingStore store=com.jachs.dashscope_sdk_java_demo.util.Utils.initPvDb2(delBase, dimension, "qianwen_a");
	
	@Test
	public void t1() throws Exception {
		MultiModalEmbedding embedding = new MultiModalEmbedding();
		
		// 1. 把查询文本转成向量
		MultiModalEmbeddingParam textParam = MultiModalEmbeddingParam.builder()
		    .model(modelName)
		    .apiKey(apiKey)
		    .contents(List.of(
		        MultiModalEmbeddingItemText.builder().text("A.jpg").build()
		    ))
		    .build();

		MultiModalEmbeddingResult textResult = embedding.call(textParam);
		List<Double> queryVector = textResult.getOutput().getEmbeddings().get(0).getEmbedding();

		Embedding queryEmbedding = Embedding.from(Utils.DoubleToFloat(queryVector));
		
		EmbeddingSearchRequest request = EmbeddingSearchRequest.builder()
		        .queryEmbedding(queryEmbedding)
		        .maxResults(10)      // 返回最相似的 10 条
		        .minScore(0.5)       // 可选：设置最低相似度阈值，过滤不相关结果[citation:4]
		        .build();

		// 2. 执行搜索
		List<EmbeddingMatch<TextSegment>> matches = store.search(request).matches();
		
		 for ( EmbeddingMatch<TextSegment> embeddingMatch : matches ) {
	            System.out.println ( "-----------**************-----------" );
	            System.out.println ( embeddingMatch.score () );
	            System.out.println ( embeddingMatch.toString () );
	            System.out.println ( "-----------**************-----------" );
	            System.out.println ( "\n" );
	        }
	}
	
	@Test
	public void t2() throws Exception {
		MultiModalEmbedding embedding = new MultiModalEmbedding();
		
		// 1. 把查询文本转成向量
		MultiModalEmbeddingParam textParam = MultiModalEmbeddingParam.builder()
		    .model(modelName)
		    .apiKey(apiKey)
		    .contents(List.of(
		        MultiModalEmbeddingItemText.builder().text("A.jpg").build(),
		        MultiModalEmbeddingItemImage.builder().image("E:\\image\\" + "A.jpg").build()
		    ))
		    .build();
		MultiModalEmbeddingResult textResult = embedding.call(textParam);
		List<Double> queryVector = textResult.getOutput().getEmbeddings().get(0).getEmbedding();

		Embedding queryEmbedding = Embedding.from(Utils.DoubleToFloat(queryVector));
		
		EmbeddingSearchRequest request = EmbeddingSearchRequest.builder()
		        .queryEmbedding(queryEmbedding)
		        .maxResults(10)      // 返回最相似的 10 条
		        .minScore(0.5)       // 可选：设置最低相似度阈值，过滤不相关结果[citation:4]
		        .build();

		// 2. 执行搜索
		List<EmbeddingMatch<TextSegment>> matches = store.search(request).matches();
		
		 for ( EmbeddingMatch<TextSegment> embeddingMatch : matches ) {
	            System.out.println ( "-----------**************-----------" );
	            System.out.println ( embeddingMatch.score () );
	            System.out.println ( embeddingMatch.toString () );
	            System.out.println ( "-----------**************-----------" );
	            System.out.println ( "\n" );
	        }
	}
}
