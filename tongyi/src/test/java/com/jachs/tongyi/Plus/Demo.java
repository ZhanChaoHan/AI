package com.jachs.tongyi.Plus;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/***
 * 通义万相-图生视频2.1-Plus，让图片变为动态视频。支持大幅度复杂运动、物理规律遵循、丰富艺术风格和影视级画面质感，
 * 指令遵循能力进一步提升，视频质量更高。
 * @author zhanchaohan
 * @see https://bailian.console.aliyun.com/#/model-market/detail/wanx2.1-i2v-plus
 */
public class Demo {
    OkHttpClient client = new OkHttpClient();
    public static String URL="https://dashscope.aliyuncs.com/api/v1/services/aigc/video-generation/video-synthesis";
    public static final MediaType JSON= MediaType.get("application/json; charset=utf-8");
    
    
    @Test
    public void t1() throws Exception {
        String json="""
            {
                "model": "wanx2.1-i2v-plus",
                "input": {
                    "prompt": "喝着红酒吃肉包子",
                    "img_url": "https://www.pnglog.com/KNXBzP.jpg"
                },
                "parameters": {
                    "prompt_extend": true
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
        String task_id="6357d5ed-392a-4488-b330-f80c4fddda4d";
        Request request = new Request.Builder()
                .addHeader ( "Authorization", "Bearer "+System.getenv("DASHSCOPE_API_KEY") )//Bearer 必须带后面是自己的key
                .url(url+task_id)
                .get ()
                .build();
        
        Response response = client.newCall(request).execute();
        
        System.out.println ( response.body().string());
    }
}
