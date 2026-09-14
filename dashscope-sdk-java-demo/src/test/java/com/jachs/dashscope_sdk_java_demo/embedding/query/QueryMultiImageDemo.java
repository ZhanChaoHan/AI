package com.jachs.dashscope_sdk_java_demo.embedding.query;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.alibaba.dashscope.embeddings.MultiModalEmbedding;
import com.alibaba.dashscope.embeddings.MultiModalEmbeddingItemImage;
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
public class QueryMultiImageDemo {
	String apiKey = "Bearer " + System.getenv("bainian_02");
	String modelName = "qwen3-vl-embedding";
	boolean delBase = false;// 是否删库
	String fileName = "C.jpeg";
	String tableName="qianwen_image";
	int dimension = 2048;

	@Test
	public void t1() throws Exception {
		MultiModalEmbedding embedding = new MultiModalEmbedding();
		MultiModalEmbeddingItemImage image = new MultiModalEmbeddingItemImage("D:\\image\\" + fileName);

		MultiModalEmbeddingParam param = MultiModalEmbeddingParam
				.builder().apiKey(apiKey)
				.model(modelName).contents(Arrays.asList(image))
				.parameters(Map.of(
						"dimension",dimension
						))
				.build();
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
