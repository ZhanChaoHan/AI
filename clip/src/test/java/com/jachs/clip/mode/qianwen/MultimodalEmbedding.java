package com.jachs.clip.mode.qianwen;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/***
 * @author zhanchaohan
 * @see https://help.aliyun.com/zh/model-studio/multimodal-embedding-api-reference?spm=a2c4g.11186623.help-menu-2400256.d_2_8_1_0.494124e17NiKul&scm=20140722.H_2712517._.OR_help-T_cn~zh-V_1#http%E8%B0%83%E7%94%A8
 */
public class MultimodalEmbedding {
	String url="https://dashscope.aliyuncs.com/api/v1/services/embeddings/multimodal-embedding/multimodal-embedding";
	String apiKey ="Bearer "+ System.getenv("bainian");
	
	@Test
	public void t1() {
		OkHttpClient client =new OkHttpClient.Builder()
	            .connectTimeout(10, TimeUnit.SECONDS) // 连接超时
	            .readTimeout(30, TimeUnit.SECONDS)    // 读取超时
	            .writeTimeout(30, TimeUnit.SECONDS)   // 写入超时
	            .build();
		

        String json = """
        {
        "model": "tongyi-embedding-vision-plus",
        "input": {
            "contents": [
                {"text": "多模态向量模型"},
                {"image": "https://img.alicdn.com/imgextra/i3/O1CN01rdstgY1uiZWt8gqSL_!!6000000006071-0-tps-1970-356.jpg"}
		            ]
		        }
        		}
        		""";

        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(json, mediaType);

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
