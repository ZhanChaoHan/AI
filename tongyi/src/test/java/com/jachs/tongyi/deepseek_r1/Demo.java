package com.jachs.tongyi.deepseek_r1;

import org.junit.jupiter.api.Test;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/***
 * @see https://bailian.console.aliyun.com/?spm=5176.29597918.0.0.1e8c7ca0ciZNed#/model-market/detail/deepseek-r1
 * @author zhanchaohan
 */
public class Demo {
    OkHttpClient client = new OkHttpClient();
    public static String URL="https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions";
    public static final MediaType JSON= MediaType.get("application/json; charset=utf-8");
    
    @Test
    public void t1() throws Exception {
        String json="""
           {
                "model": "deepseek-r1",
                "messages": [
                    {
                        "role": "user", 
                        "content": "存多少钱才能体面退休"
                    }
                ],
                "stream": true,
                "stream_options": {
                    "include_usage": true
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
