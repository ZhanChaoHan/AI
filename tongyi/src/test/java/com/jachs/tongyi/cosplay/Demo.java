package com.jachs.tongyi.cosplay;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/***
 * 本文介绍通义万相-Cosplay动漫人物模型的输入输出参数。通过输入人像图片和卡通形象图片，可快速生成人物卡通写真。
 * @see https://help.aliyun.com/zh/model-studio/user-guide/cosplay-anime-character-generation?spm=0.0.0.i1
 * @author zhanchaohan
 */
public class Demo {
    OkHttpClient client = new OkHttpClient();
    public static String URL="https://dashscope.aliyuncs.com/api/v1/services/aigc/image-generation/generation";
    public static final MediaType JSON= MediaType.get("application/json; charset=utf-8");
    
    @Test
    public void t1() throws Exception {
        String json="""
             {
                "model": "wanx-style-cosplay-v1",
                "input": {
                    "model_index": 1,
                    "face_image_url": "https://www.pnglog.com/xx9PWT.jpg",
                    "template_image_url": "https://www.pnglog.com/qh2lx7.jpg"
                }
            }
             """;
        
        RequestBody body = RequestBody.create(json, JSON);
        
        Request request = new Request.Builder()
                .addHeader ( "X-DashScope-Async", "enable" )
                .addHeader ( "Authorization", "Bearer "+System.getenv("DASHSCOPE_API_KEY") )//Bearer 必须带后面是自己的key
                .url(URL)
                .post(body)
                .build();
        
        Response response = client.newCall(request).execute();
        
        System.out.println ( response.body().string());
    }
    
    //取去task_id获取视频
    @Test
    public void getMp4() throws IOException {
        String url="https://dashscope.aliyuncs.com/api/v1/tasks/";
        String task_id="c7f433fb-c7f8-4afe-ac50-473f0ca189d7";
        Request request = new Request.Builder()
                .addHeader ( "Authorization", "Bearer "+System.getenv("DASHSCOPE_API_KEY") )//Bearer 必须带后面是自己的key
                .url(url+task_id)
                .get ()
                .build();
        
        Response response = client.newCall(request).execute();
        
        System.out.println ( response.body().string());
    }
}
