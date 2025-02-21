package com.jachs.tongyi.word_art;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/****
 * @see https://bailian.console.aliyun.com/?spm=5176.29597918.J__Xz0dtrgG-8e2H7vxPlPy.7.1e8c7ca0IZzWV7#/model-market/detail/wordart-surnames?tabKey=sdk
 * @author hanchaohan
 */
public class Demo {
    OkHttpClient client = new OkHttpClient();
    public static String URL="https://dashscope.aliyuncs.com/api/v1/services/aigc/wordart/surnames";
    public static final MediaType JSON= MediaType.get("application/json; charset=utf-8");
    
    @Test
    public void t1() throws Exception {
        String json="""
           {
                "model": "wordart-surnames",
                "prompt": "白云，蓝天", 
                "input":{
                    "surname": "诜羽",
                    "style": "tranquil_countryside"
                },
                "parameters": {
                    "n": 1
                }
            }
             """;
        
        //自定义
        /* String json="""
               {
                    "text": {
                        "font_name": "gufeng1",
                        "ttf_url": null,
                        "text_strength": 0.5,
                        "text_inverse": false
                    },
                    "surname": "赵",
                    "style": "diy",
                    "prompt": "高山，流水", 
                    "ref_image_url": null
                },
                "parameters": {
                    "n": 1
                }
            }
                  """;*/
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
    
    
   //取去task_id获取图片
    @Test
    public void getMp4() throws IOException {
        String url="https://dashscope.aliyuncs.com/api/v1/tasks/";
        String task_id="e7154218-b856-4867-a891-0701763af20d";
        Request request = new Request.Builder()
                .addHeader ( "Authorization", "Bearer "+System.getenv("DASHSCOPE_API_KEY") )//Bearer 必须带后面是自己的key
                .url(url+task_id)
                .get ()
                .build();
        
        Response response = client.newCall(request).execute();
        
        System.out.println ( response.body().string());
    }
}
