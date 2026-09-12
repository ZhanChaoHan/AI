package com.jachs.qianwen.embedding_model;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.Test;

import com.alibaba.dashscope.embeddings.MultiModalEmbedding;
import com.alibaba.dashscope.embeddings.MultiModalEmbeddingParam;
import com.alibaba.dashscope.embeddings.MultiModalEmbeddingResult;

import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;



/***
 * @author zhanchaohan
 */
public class MultimodalEmbeddingDemo {
	String apiKey ="Bearer "+ System.getenv("bainian_02");
	int dimension=1024;
	
	private String Base64Image(File imageFile) throws Exception {
		byte[] imageBytes = Files.readAllBytes(imageFile.toPath());
        String base64Image = Base64.encodeBase64String(imageBytes);
        // 注意：必须添加前缀，格式取决于图片类型，这里以 png 为例
        String dataUri = "data:image/png;base64," + base64Image;
        return base64Image;
	}
	
	
	@Test
	public void t1() throws Exception {
				PgVectorEmbeddingStore store=shared.Utils.initPvDb2(true,dimension,"qianwen");
		
	            MultiModalEmbedding multiModalEmbedding = new MultiModalEmbedding();
	            
	            MultiModalEmbeddingParam param = MultiModalEmbeddingParam.builder()
	                    .model("multimodal-embedding-v1")
	                    .apiKey(apiKey)
	                    .parameters(null)
	                    .build();

	            Map<String, Object> contentMap = new HashMap<>();
	            contentMap.put("image", Base64Image(new File("E:\\image\\A.png")));
	            Map<String, Object> parametersMap = new HashMap<>();
	            parametersMap.put("dimension",dimension);
	            
	            java.util.List<Map<String, Object>> inputs = Arrays.asList(contentMap,parametersMap);
	            MultiModalEmbeddingResult result = multiModalEmbedding.call(param);

	            List<Double> vector = result.getOutput().getEmbeddings().get(0).getEmbedding();
	            float[] floatVector = new float[vector.size()];
	            for (int i = 0; i < vector.size(); i++) {
	                floatVector[i] =vector.get(i).floatValue();
	            }
	            
	            Embedding embedding = Embedding.from(floatVector);
	            Metadata metadata=new Metadata();
	            metadata.put("name", "A.png");
	            
	            TextSegment textSegment=TextSegment.from("",metadata);
	            
	            
	            store.add(embedding,textSegment);
	      }
	
	private static String encodeImageToBase64(String imagePath) throws Exception {
        File file = new File(imagePath);
        byte[] fileContent = Files.readAllBytes(file.toPath());
        return Base64.encodeBase64String(fileContent);
    }
}
