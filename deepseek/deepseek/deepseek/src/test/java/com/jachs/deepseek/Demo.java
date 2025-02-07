package com.jachs.deepseek;


import org.junit.jupiter.api.Test;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/***
 * @author zhanchaohan
 */
public class Demo {
    //deepseek接口地址
    public static String URL="https://api.deepseek.com/chat/completions";
    OkHttpClient client = new OkHttpClient();
    public static final MediaType JSON= MediaType.get("application/json; charset=utf-8");
    
    @Test
    public void t1() throws Exception {
        String json="""
                {
                    "model": "deepseek-chat",
                    "messages": [
                      {"role": "system", "content": "锄禾日当午下一句是什么？"},
                      {"role": "user", "content": "你好用英文怎么表达？"}
                    ],
                    "stream": false
                  }
                """;
        
        RequestBody body = RequestBody.create(json, JSON);
        
        Request request = new Request.Builder()
                .addHeader ( "Authorization", "Bearer sk-1a29221c6ac04481a4b9d7f07318f591" )//Bearer 必须带后面是自己的key
                .url(URL)
                .post(body)
                .build();
        
        Response response = client.newCall(request).execute();
        
        System.out.println ( response.body().string());
    }
    
}
