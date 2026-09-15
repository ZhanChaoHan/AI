package com.jachs.dashscope_sdk_java_demo.embedding.query;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.alibaba.dashscope.embeddings.MultiModalEmbedding;
import com.alibaba.dashscope.embeddings.MultiModalEmbeddingItemText;
import com.alibaba.dashscope.embeddings.MultiModalEmbeddingParam;
import com.alibaba.dashscope.embeddings.MultiModalEmbeddingResult;
import com.jachs.dashscope_sdk_java_demo.util.Utils;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;

/***
 * @author zhanchaohan
 */
public class TextQueryDemo {
	String apiKey = "Bearer " + System.getenv("bainian_02");
	boolean delBase = false;// 是否删库

	String tableName = "qianwen_text";
	String modelName = "multimodal-embedding-v1";
	int dimension = 1024;

	MultiModalEmbedding embedding = new MultiModalEmbedding();

	@Test
	public void t1() throws Exception {
		String file="青海长云暗雪山，孤城遥望玉门关。";
		
		MultiModalEmbeddingItemText text = MultiModalEmbeddingItemText.builder().text(file).build();

		MultiModalEmbeddingParam param = MultiModalEmbeddingParam.builder()
				.apiKey(apiKey).model(modelName)
				.contents(Arrays.asList(text))
				.parameters(Map.of("dimension", dimension)).build();
		MultiModalEmbeddingResult result = embedding.call(param);

		System.out.print(result);

		PgVectorEmbeddingStore store = Utils.initPvDb2(delBase, dimension, tableName);

		List<Double> vector = result.getOutput().getEmbeddings().get(0).getEmbedding();

		Embedding queryEmbedding = Embedding.from(Utils.DoubleToFloat(vector));
		
		EmbeddingSearchRequest request = EmbeddingSearchRequest.builder()
		        .queryEmbedding(queryEmbedding)
		        .maxResults(10)
		        .minScore(0.5)
		        .build();

		List<EmbeddingMatch<TextSegment>> matches = store.search(request).matches();
		
		Utils.printEmbeddingMatch(matches);

	}
}
