package com.jachs.tongyi.Emoji;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/***
 * 表情包Emoji是一款人脸动效视频生成模型，可基于通过Emoji-detect模型检测的人物肖像图片和预设的人脸动态模板，生成人脸动效视频。
 * 
 * @author zhanchaohan
 * @see https://bailian.console.aliyun.com/?spm=5176.29597918.J__Xz0dtrgG-8e2H7vxPlPy.8.50be7ca0hBGBhM#/model-market/detail/emoji-v1
 */
public class Demo {
    OkHttpClient client = new OkHttpClient();
    public static String URL="https://dashscope.aliyuncs.com/api/v1/services/aigc/image2video/video-synthesis";
    public static final MediaType JSON= MediaType.get("application/json; charset=utf-8");
    
    @Test
    public void t1() throws Exception {
        String json="""
              {
                "model": "emoji-v1",
                "input": {
                    "image_url": "https://www.pnglog.com/Cm8UoW.png",
                    "driven_id": "dagong_zhuakuang",
                    "face_bbox": [344,180,515,351],
                    "ext_bbox": [242,68,617,443]
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
        String task_id="579ed2a1-5a1e-4b92-84ad-b336875e6ceb";
        Request request = new Request.Builder()
                .addHeader ( "Authorization", "Bearer "+System.getenv("DASHSCOPE_API_KEY") )//Bearer 必须带后面是自己的key
                .url(url+task_id)
                .get ()
                .build();
        
        Response response = client.newCall(request).execute();
        
        System.out.println ( response.body().string());
    }
}
