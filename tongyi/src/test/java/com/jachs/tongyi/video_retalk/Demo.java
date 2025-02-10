package com.jachs.tongyi.video_retalk;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/***
 * @author zhanhaohan
 * @see https://help.aliyun.com/zh/model-studio/developer-reference/videoretalk-api?spm=a2c4g.11186623.0.0.6b25326bcDMz5e
 */
public class Demo {
    OkHttpClient client = new OkHttpClient();
    public static String URL="https://dashscope.aliyuncs.com/api/v1/services/aigc/image2video/video-synthesis/";
    public static final MediaType JSON= MediaType.get("application/json; charset=utf-8");
    
    @Test
    public void t1() throws Exception {
        String json="""
              {
                "model": "videoretalk",
                "input": {
                    "video_url": "http://xxx/1.mp4",
                    "audio_url": "http://xxx/1.wav",
                    "ref_image_url": ""
                 },
                "parameters": {
                    "video_extension": false
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
}
