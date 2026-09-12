package com.jachs.dashscope_sdk_java_demo.embedding.add;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.alibaba.dashscope.embeddings.MultiModalEmbedding;
import com.alibaba.dashscope.embeddings.MultiModalEmbeddingItemImage;
import com.alibaba.dashscope.embeddings.MultiModalEmbeddingItemText;
import com.alibaba.dashscope.embeddings.MultiModalEmbeddingParam;
import com.alibaba.dashscope.embeddings.MultiModalEmbeddingResult;
import com.jachs.dashscope_sdk_java_demo.util.Utils;

import dev.langchain4j.community.model.dashscope.QwenEmbeddingModel;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;

/***
 * @author zhanchaohan
 */
public class MultiModalEmbeddingDemo1 {
	String apiKey = "Bearer " + System.getenv("bainian_02");
	String modelName = "qwen2.5-vl-embedding";
	boolean delBase = false;// 是否删库
	String fileName = "A.jpg";
	int dimension = 1024;

	@Test
	public void t1() throws Exception {
		MultiModalEmbedding embedding = new MultiModalEmbedding();
		MultiModalEmbeddingItemText text = MultiModalEmbeddingItemText.builder().text(fileName).build();
		MultiModalEmbeddingItemImage image = new MultiModalEmbeddingItemImage("E:\\image\\" + fileName);

		MultiModalEmbeddingParam param = MultiModalEmbeddingParam
				.builder().apiKey(apiKey)
				.model(modelName).contents(Arrays.asList(image,text))
				.build();
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("dimension", dimension);
		param.setParameters(parameters);
		MultiModalEmbeddingResult result = embedding.call(param);

		System.out.print(result);

		PgVectorEmbeddingStore store = Utils.initPvDb2(delBase, dimension, "qianwen_a");

		List<Double> vector = result.getOutput().getEmbeddings().get(0).getEmbedding();
		float[] floatVector = new float[vector.size()];
		for (int i = 0; i < vector.size(); i++) {
			floatVector[i] = vector.get(i).floatValue();
		}

		Embedding emb = Embedding.from(floatVector);
		Metadata metadata = new Metadata();
		metadata.put("name", fileName);

		TextSegment textSegment = TextSegment.from(fileName, metadata);

		store.add(emb, textSegment);

	}

	@Test
	public void t2() {
		QwenEmbeddingModel model = QwenEmbeddingModel.builder()
				.apiKey(apiKey).modelName(modelName).build();
		
	}
}
