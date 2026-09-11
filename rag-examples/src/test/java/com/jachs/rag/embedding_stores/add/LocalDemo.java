package com.jachs.rag.embedding_stores.add;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.Test;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/***
text 字段存储内容
text 字段是向量检索时的核心匹配依据，‌不能直接存图片二进制或 base64‌，推荐存储以下内容：
图片通过视觉大模型（如 Qwen3-VL-Embedding）生成的‌详细文本描述‌，包含主体、场景、文字、色彩等关键信息
图片中识别提取出的‌OCR 文字内容‌，确保图片里的文字片段能被文本检索命中
图片的‌核心语义摘要‌，控制在合理长度，避免超出 Embedding 模型的输入 Token 上限
图片关联的人工标注说明、业务侧的补充描述文本


metadata 字段存储内容
metadata 作为 JSONB 类型的元数据，用于存储可过滤、可追溯的结构化信息，推荐填充以下维度：
‌基础文件属性‌：图片的本地文件路径、文件名、文件扩展名、文件大小、图片宽高、MIME 类型
‌业务关联信息‌：图片所属的知识库 ID、分类标签、上传时间、上传人 ID、来源渠道
‌检索辅助字段‌：图片的原始 URL（如果是网络图片）、关联的业务单据 ID、权限控制标识
‌运维追溯字段‌：向量化处理的时间戳、使用的 Embedding 模型名称、模型版本号
 * @author zhanchaohan
 */
public class LocalDemo {
	String url="https://dashscope.aliyuncs.com/api/v1/services/embeddings/multimodal-embedding/multimodal-embedding";
	String apiKey ="Bearer "+ System.getenv("bainian_02");
	
	public static String toDataUri(Path imagePath, String format) throws IOException {
	     byte[] imageBytes = Files.readAllBytes(imagePath);
	     String base64 =  Base64.encodeBase64String(imageBytes);
	     return "data:image/" + format + ";base64," + base64;
	}
	
	public String doHttp() throws Exception {
		OkHttpClient client =new OkHttpClient.Builder()
	            .connectTimeout(10, TimeUnit.SECONDS) // 连接超时
	            .readTimeout(30, TimeUnit.SECONDS)    // 读取超时
	            .writeTimeout(30, TimeUnit.SECONDS)   // 写入超时
	            .build();
		

		File file = new File("D:\\猫狗.jpg");
		JSONObject payload = new JSONObject();
        payload.put("model", "qwen2.5-vl-embedding");
        
        JSONObject parameters = new JSONObject();
        payload.put("dimension", "1024");//向量维度
        
        JSONObject contentItem = new JSONObject();
        contentItem.put("image", toDataUri(file.toPath(), "jpg"));
        
        payload.put("input", contentItem); 
        payload.put("parameters", parameters); 

        System.out.println(payload.toString());
        
        
        RequestBody body = RequestBody.create(
                payload.toString(), 
                MediaType.parse("application/json; charset=utf-8")
            );

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", apiKey)
                .post(body)
                .build();

       
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
            	String bodyMsg=response.body().string();
            	
                System.out.println("创建成功: " + bodyMsg);
                return bodyMsg;
            } else {
                System.out.println("错误代码: " + response.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
	}
	
	
	
	@Test
	public void t1() throws Exception {
		PgVectorEmbeddingStore store=PgVectorEmbeddingStore.builder()
        .host("localhost")
        .port(5432)
        .database("langchat")
        .user("postgres")
        .password("12345678")
        .table("image_embeddings")
        .dropTableFirst(true)
        .dimension(1024)  // 向量维度
        .build();
		
		JSONObject dataJson=JSONObject.parseObject(doHttp());
		
		JSONArray Arr=dataJson.getJSONObject("output").getJSONArray("embeddings");
		
		JSONArray floatArr=Arr.getJSONObject(0).getJSONArray("embedding");
		
		List<Float> vector=new ArrayList<Float>();
		
		for (int kk = 0; kk < floatArr.size(); kk++) {
			vector.add(floatArr.getFloat(kk));
		}
		
		Embedding embedding = Embedding.from(vector);
		Metadata metadata=new Metadata();
		metadata.put("filename", "c.jpg");
		
		TextSegment ts=new TextSegment("文本", metadata);
		
		store.add(embedding,ts);
		
	}
}
