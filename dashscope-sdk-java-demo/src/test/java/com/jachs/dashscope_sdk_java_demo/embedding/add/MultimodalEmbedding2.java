package com.jachs.dashscope_sdk_java_demo.embedding.add;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

import com.alibaba.fastjson.JSONObject;

import cn.hutool.core.io.FileUtil;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/***
 * @author zhanchaohan
 * @see https://help.aliyun.com/zh/model-studio/multimodal-embedding-api-reference?spm=a2c4g.11186623.help-menu-2400256.d_2_8_1_0.494124e17NiKul&scm=20140722.H_2712517._.OR_help-T_cn~zh-V_1#http%E8%B0%83%E7%94%A8
 * @see https://help.aliyun.com/zh/model-studio/embedding?spm=a2c4g.11186623.0.i2#java-http-h4
 */
public class MultimodalEmbedding2 {
	String url="https://dashscope.aliyuncs.com/api/v1/services/embeddings/multimodal-embedding/multimodal-embedding";
	String apiKey ="Bearer "+ System.getenv("bainian_02");
	
	public static String toDataUri(Path imagePath, String format) throws IOException {
	     byte[] imageBytes = Files.readAllBytes(imagePath);
	     String base64 =  Base64.encodeBase64String(imageBytes);
	     return "data:image/" + format + ";base64," + base64;
	}
	   
	@Test
	public void t1() throws Exception {
		OkHttpClient client =new OkHttpClient.Builder()
	            .connectTimeout(10, TimeUnit.SECONDS) // 连接超时
	            .readTimeout(30, TimeUnit.SECONDS)    // 读取超时
	            .writeTimeout(30, TimeUnit.SECONDS)   // 写入超时
	            .build();
		

		File file = new File("E:\\image\\D.jpg");
		String mimeType = FileUtil.getMimeType(file.toPath());
		
		JSONObject payload = new JSONObject();
        payload.put("model", "qwen2.5-vl-embedding");
        
        JSONObject parameters = new JSONObject();
        payload.put("dimension", "2048");//向量维度
        
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
                System.out.println("创建成功: " + response.body().string());
            } else {
                System.out.println("错误代码: " + response.code());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
