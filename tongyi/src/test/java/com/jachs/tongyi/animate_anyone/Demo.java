package com.jachs.tongyi.animate_anyone;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/***
 * 未通过
 * @author zhanchaohan
 * @see https://bailian.console.aliyun.com/?spm=5176.29597918.J__Xz0dtrgG-8e2H7vxPlPy.8.50be7ca0hBGBhM#/model-market/detail/animate-anyone-gen2
 */
public class Demo {
    public static String URL="https://dashscope.aliyuncs.com/api/v1/services/aigc/image2video/video-synthesis/";
    OkHttpClient client = new OkHttpClient();
    public static final MediaType JSON= MediaType.get("application/json; charset=utf-8");
    
    @Test
    public void t1() throws Exception {
        String json="""
                   {
                    "model": "animate-anyone-gen2",
                    "input": {
                        "image_url": "https://dashscope-result-bj.oss-cn-beijing.aliyuncs.com/1d/f3/20250208/59320e4c/3cce2027-274e-44ca-b38c-dfa94d48b6f2-1.png?Expires=1739085705&OSSAccessKeyId=LTAI5tQZd8AEcZX6KZV4G8qL&Signature=Ef4Ji3dOeopKBRDniTDlgVJdKq4%3D",
                        "template_id": "AACT.xxx.xxx-xxx.xxx"
                    },
                      "parameters": {
                         "use_ref_img_bg": false,
                         "video_ratio": "9:16"
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
        String task_id="f61bb0f0-c7eb-4b22-9a8a-2a72b8a06ba9";
        Request request = new Request.Builder()
                .addHeader ( "Authorization", "Bearer "+System.getenv("DASHSCOPE_API_KEY") )//Bearer 必须带后面是自己的key
                .url(url+task_id)
                .get ()
                .build();
        
        Response response = client.newCall(request).execute();
        
        System.out.println ( response.body().string());
    }
}
