package com.jachs.dashscope_sdk_java_demo.embedding.add;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

import com.alibaba.dashscope.embeddings.MultiModalEmbedding;
import com.alibaba.dashscope.embeddings.MultiModalEmbeddingItemImage;
import com.alibaba.dashscope.embeddings.MultiModalEmbeddingParam;
import com.alibaba.dashscope.embeddings.MultiModalEmbeddingResult;
import com.jachs.dashscope_sdk_java_demo.util.Utils;

import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;

/***
 * @author zhanchaohan
 */
public class MultiImageDemo {
	String apiKey = "Bearer " + System.getenv("bainian_02");
	boolean delBase = false;// 是否删库
	List<String>fileList=Arrays.asList(
			"A.jpg","AA.png","B.jpeg","BB.png","C.jpeg"
			,"CC.png","D.jpg","DD.png","EE.png");
	
	String imgPath="D:\\image\\";
	String tableName="qianwen_image";
	String modelName = "multimodal-embedding-v1";
	int dimension = 1024;

	
	MultiModalEmbedding embedding = new MultiModalEmbedding();
	
	@Test
	public void t1() throws Exception {
		for (String file : fileList) {
			MultiModalEmbeddingItemImage image = new MultiModalEmbeddingItemImage(imgPath + file);
	
			MultiModalEmbeddingParam param = MultiModalEmbeddingParam
					.builder().apiKey(apiKey)
					.model(modelName).contents(Arrays.asList(image))
					.parameters(Map.of(
							"dimension",dimension,
							"instruct","向量化图片"
							))
					.build();
			MultiModalEmbeddingResult result = embedding.call(param);
	
			System.out.print(result);
	
			PgVectorEmbeddingStore store = Utils.initPvDb2(delBase, dimension, tableName);
	
			List<Double> vector = result.getOutput().getEmbeddings().get(0).getEmbedding();
	
			Embedding emb = Embedding.from(Utils.DoubleToFloat(vector));
			Metadata metadata = new Metadata();
			metadata.put("name", file);
	
			TextSegment textSegment = TextSegment.from(file, metadata);
	
			store.add(emb, textSegment);
		}
	}

	
	@Test
	public void t2() throws Exception {
		String img="B.jpeg\"";
		byte[] fileBytes = Files.readAllBytes(Paths.get("D:\\image\\"+img));
		String base64Img = Base64.encodeBase64String(fileBytes);
		String fullBase64Url = "data:image/jpeg;base64," + base64Img;
		
		MultiModalEmbeddingItemImage image =MultiModalEmbeddingItemImage
				.builder().image(fullBase64Url).build();
		
		MultiModalEmbeddingParam param = MultiModalEmbeddingParam
				.builder().apiKey(apiKey)
				.model(modelName).contents(Arrays.asList(image))
				.parameters(Map.of(
						"dimension",dimension,
						"instruct","向量化图片"
						))
				.build();
		MultiModalEmbeddingResult result = embedding.call(param);

		System.out.print(result);

		PgVectorEmbeddingStore store = Utils.initPvDb2(delBase, dimension, tableName);

		List<Double> vector = result.getOutput().getEmbeddings().get(0).getEmbedding();

		Embedding emb = Embedding.from(Utils.DoubleToFloat(vector));
		Metadata metadata = new Metadata();
		metadata.put("name", img);

		TextSegment textSegment = TextSegment.from(img, metadata);

		store.add(emb, textSegment);

	}
	
}
