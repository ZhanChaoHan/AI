package com.jachs.tongyi.wenshengshiping;

import org.junit.jupiter.api.Test;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/***
 * @see https://bailian.console.aliyun.com/?spm=5176.29597918.J_SEsSjsNv72yRuRFS2VknO.2.1e8c7ca0K0OWyR&accounttraceid=1f0aebc0c4094ea0b798b4afd17a4692qwes#/model-market/detail/wanx2.1-t2v-plus?tabKey=sdk
 * @author zhanchaohan
 */
public class Demo {
    OkHttpClient client = new OkHttpClient();
    public static String URL="https://dashscope.aliyuncs.com/api/v1/services/aigc/video-generation/video-synthesis";
    public static final MediaType JSON= MediaType.get("application/json; charset=utf-8");
    
    @Test
    public void t1() throws Exception {
        String json="""
             {
                "model": "wanx2.1-t2v-turbo",
                "input": {
                    "prompt": "金发碧眼的美女在海滩晒太阳"
                },
                "parameters": {
                    "size": "1280*720"
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
