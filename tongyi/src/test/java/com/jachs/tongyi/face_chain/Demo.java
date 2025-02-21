package com.jachs.tongyi.face_chain;

import org.junit.jupiter.api.Test;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/***
未实现
物写真2.0支持人物形象训练lora模式和人物形象免训练trainfree模式。
1）人物形象训练lora模式：基于人物形象训练模型已经得到的人物形象lora，可以继续通过人物生成写真模型完成该形象的高保真写真生成，支持多种预设风格，包括证件照、商务写真、复古风、夏日运动等风格，同时支持客户自定义风格模板上传方式生成自定义人物写真照。
2）人物形象免训练trainfree模式【推荐】：同时上传一组包含用户正脸单人照（至少一张）和客户自定义风格模板，通过人物生成写真模型直接一键免训练极速生成人物写真照，仅支持客户自定义风格模板上传方式免训练trainfree生成写真。
 * @see https://bailian.console.aliyun.com/?spm=5176.29597918.J__Xz0dtrgG-8e2H7vxPlPy.7.1e8c7ca0IZzWV7#/model-market/detail/facechain-generation?tabKey=sdk
 * @author zhanchaohan
 */
public class Demo {
    OkHttpClient client = new OkHttpClient();
    public static String URL="https://dashscope.aliyuncs.com/api/v1/services/aigc/album/gen_potrait";
    public static final MediaType JSON= MediaType.get("application/json; charset=utf-8");
    
    @Test
    public void t1() throws Exception {
        String json="""
             {
                "model": "facechain-generation",
                "parameters": {
                "style": "f_idcard_female", 
                "size": "768*1024",
                "n":4
                },
                "resources": [
                {
                "resource_id": "women_model",
                "resource_type": "facelora"
                }
                ]
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
