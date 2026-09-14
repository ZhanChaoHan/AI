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
import dev.langchain4j.data.image.Image;
import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;

/***
 * @author zhanchaohan
 */
public class MultiImageDemo {
	String apiKey = "Bearer " + System.getenv("bainian_02");
	String modelName = "qwen3-vl-embedding";
	boolean delBase = false;// 是否删库
	String fileName = "C.jpeg";
	String imgPath="D:\\image\\";
	String tableName="qianwen_image";
	int dimension = 2048;

	
	MultiModalEmbedding embedding = new MultiModalEmbedding();
	
	@Test
	public void t1() throws Exception {
		MultiModalEmbeddingItemImage image = new MultiModalEmbeddingItemImage(imgPath + fileName);

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
		metadata.put("name", fileName);

		TextSegment textSegment = TextSegment.from(fileName, metadata);

		store.add(emb, textSegment);

	}

	
	@Test
	public void t2() throws Exception {
		byte[] fileBytes = Files.readAllBytes(Paths.get("D:\\image\\B.jpeg"));
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
		metadata.put("name", fileName);

		TextSegment textSegment = TextSegment.from(fileName, metadata);

		store.add(emb, textSegment);

	}
	
}
