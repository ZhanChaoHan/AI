package com.jachs.dashscope_sdk_java_demo.embedding.query;

import java.util.List;

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
	String tableName="qianwen_a";
	String modelName = "qwen3-vl-embedding";
	boolean delBase=false;//是否删库
	int dimension =2560;
	
	PgVectorEmbeddingStore store=com.jachs.dashscope_sdk_java_demo.util.Utils.initPvDb2(delBase, dimension, tableName);
	
	@Test
	public void t1() throws Exception {
		MultiModalEmbedding embedding = new MultiModalEmbedding();
		
		MultiModalEmbeddingParam textParam = MultiModalEmbeddingParam.builder()
		    .model(modelName)
		    .apiKey(apiKey)
		    .contents(List.of(
		        MultiModalEmbeddingItemText.builder().text("河畔和红颜色的花").build()
		    ))
		    .build();

		MultiModalEmbeddingResult textResult = embedding.call(textParam);
		List<Double> queryVector = textResult.getOutput().getEmbeddings().get(0).getEmbedding();

		Embedding queryEmbedding = Embedding.from(Utils.DoubleToFloat(queryVector));
		
		EmbeddingSearchRequest request = EmbeddingSearchRequest.builder()
		        .queryEmbedding(queryEmbedding)
		        .maxResults(10)     
		        .minScore(0.5)       
		        .build();

		List<EmbeddingMatch<TextSegment>> matches = store.search(request).matches();
		
		Utils.printEmbeddingMatch(matches);
	}
	
	@Test
	public void t2() throws Exception {
		MultiModalEmbedding embedding = new MultiModalEmbedding();
		
		MultiModalEmbeddingParam textParam = MultiModalEmbeddingParam.builder()
		    .model(modelName)
		    .apiKey(apiKey)
		    .contents(List.of(
		        MultiModalEmbeddingItemImage.builder().image("E:\\image\\" + "A.jpg").build()
		    ))
		    .build();
		MultiModalEmbeddingResult textResult = embedding.call(textParam);
		List<Double> queryVector = textResult.getOutput().getEmbeddings().get(0).getEmbedding();

		Embedding queryEmbedding = Embedding.from(Utils.DoubleToFloat(queryVector));
		
		EmbeddingSearchRequest request = EmbeddingSearchRequest.builder()
		        .queryEmbedding(queryEmbedding)
		        .maxResults(10)
		        .minScore(0.5)
		        .build();

		List<EmbeddingMatch<TextSegment>> matches = store.search(request).matches();
		Utils.printEmbeddingMatch(matches);
	}
	
	
	@Test
	public void t3() throws Exception {
		MultiModalEmbedding embedding = new MultiModalEmbedding();
		
		MultiModalEmbeddingParam textParam = MultiModalEmbeddingParam.builder()
		    .model(modelName)
		    .apiKey(apiKey)
		    .contents(List.of(
		        MultiModalEmbeddingItemText.builder().text("一只小鸟").build(),
		        MultiModalEmbeddingItemImage.builder().image("E:\\image\\" + "A.jpg").build()
		    ))
		    .build();
		MultiModalEmbeddingResult textResult = embedding.call(textParam);
		List<Double> queryVector = textResult.getOutput().getEmbeddings().get(0).getEmbedding();

		Embedding queryEmbedding = Embedding.from(Utils.DoubleToFloat(queryVector));
		
		EmbeddingSearchRequest request = EmbeddingSearchRequest.builder()
		        .queryEmbedding(queryEmbedding)
		        .maxResults(10)
		        .minScore(0.5)
		        .build();

		List<EmbeddingMatch<TextSegment>> matches = store.search(request).matches();
		Utils.printEmbeddingMatch(matches);
	}
}
