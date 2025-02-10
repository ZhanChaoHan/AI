package com.jachs.tongyi.Emoji;

import org.junit.jupiter.api.Test;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/***
 * 表情包Emoji-detect是辅助表情包Emoji生成的图像检测模型，用于检测图像中的人物形象是否符合视频生成要求。
 * @author zhanchaohan
 * @see https://help.aliyun.com/zh/model-studio/developer-reference/emoji-detect-api?spm=0.0.0.i11
 */
public class EmojiDetect {
    OkHttpClient client = new OkHttpClient();
    public static String URL="https://dashscope.aliyuncs.com/api/v1/services/aigc/image2video/face-detect";
    public static final MediaType JSON= MediaType.get("application/json; charset=utf-8");
    
    @Test
    public void t1() throws Exception {
        String json="""
             {
                "model": "emoji-detect-v1",
                "input": {
                    "image_url": "https://www.pnglog.com/Cm8UoW.png"
                },
                "parameters": {
                    "ratio":"1:1"
                }
              }
             """;
        
        RequestBody body = RequestBody.create(json, JSON);
        
        Request request = new Request.Builder()
                .addHeader ( "Authorization", "Bearer "+System.getenv("DASHSCOPE_API_KEY") )//Bearer 必须带后面是自己的key
                .url(URL)
                .post(body)
                .build();
        
        Response response = client.newCall(request).execute();
        
        System.out.println ( response.body().string());
    }
}
