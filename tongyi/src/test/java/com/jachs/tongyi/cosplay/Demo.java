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
                    "face_image_url": "oss://dashscope-instant/ed977b897d2a9170f016076c6557f557/2025-10-31/d74d4dd6-82ce-4a84-97e5-5610b0e74e5a/D.jpg",
                    "template_image_url": "https://vd3.bdstatic.com/mda-rds6awczicejx76t/1745728063/mda-rds6awczicejx76t00048662.jpg?for=bg"
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
    
    @Test
    public void t2() throws IOException {
        String json="""
               {
                "model": "wanx-style-repaint-v1",
                "input": {
                    "image_url": "https://files.imagetourl.net/uploads/1761896676490-8f39ffd9-7240-4bb3-9ce4-0750efc72324.JPG",
                    "style_ref_url": "https://vigen-video.oss-cn-shanghai.aliyuncs.com/demo_image/style_example.png",
                    "style_index": 37
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
        String task_id="92352b82-a672-4f83-87d7-be723a1b17ca";
        Request request = new Request.Builder()
                .addHeader ( "Authorization", "Bearer "+System.getenv("DASHSCOPE_API_KEY") )//Bearer 必须带后面是自己的key
                .url(url+task_id)
                .get ()
                .build();
        
        Response response = client.newCall(request).execute();
        
        System.out.println ( response.body().string());
    }
}
