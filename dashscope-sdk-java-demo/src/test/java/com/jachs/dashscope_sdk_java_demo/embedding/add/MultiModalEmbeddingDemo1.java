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
import com.jachs.dashscope_sdk_java_demo.App;
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
	String modelName = "qwen3-vl-embedding";
	String tableName="qianwen_a";
	boolean delBase = false;// 是否删库
	int dimension = 2560;

	
	Map<String,String>maps=Map.of(
			"一只小鸟","E:\\image\\A.jpg",
			"大海洋和明月","E:\\image\\B.jpeg",
			"一片树林","E:\\image\\C.jpeg",
			"河畔和红颜色的花","E:\\image\\D.jpg"
			);
	
	@Test
	public void t1() throws Exception {
		MultiModalEmbedding embedding = new MultiModalEmbedding();
		PgVectorEmbeddingStore store = Utils.initPvDb2(delBase, dimension, tableName);
		
		for (String key : maps.keySet()) {
			MultiModalEmbeddingItemText text = MultiModalEmbeddingItemText.builder().text(key).build();
			MultiModalEmbeddingItemImage image = new MultiModalEmbeddingItemImage(maps.get(key));
			MultiModalEmbeddingParam param = MultiModalEmbeddingParam
					.builder().apiKey(apiKey)
					.model(modelName).contents(Arrays.asList(image,text))
					.parameters(Map.of(
							"dimension",dimension,
							"enable_fusion",true
							))
					.build();
			MultiModalEmbeddingResult result = embedding.call(param);
	
			System.out.print(result);
			List<Double> vector = result.getOutput().getEmbeddings().get(0).getEmbedding();
	
			Embedding emb = Embedding.from(Utils.DoubleToFloat(vector));
			Metadata metadata=Metadata.from(
					Map.of("path",maps.get(key),
					"info",key));
			
			TextSegment textSegment = TextSegment.from(key, metadata);
	
			store.add(emb, textSegment);
		}
	}

}
