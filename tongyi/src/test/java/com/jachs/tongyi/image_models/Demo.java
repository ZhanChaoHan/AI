package com.jachs.tongyi.image_models;


import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesis;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisListResult;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisParam;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisResult;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.task.AsyncTaskListParam;
import com.alibaba.dashscope.utils.JsonUtils;



/***
 * @author zhanchaohan
 */
public class Demo {
    
    public static void basicCall() throws ApiException, NoApiKeyException {
//        String prompt = "全身镜头，18岁的美国女孩，高中校园服饰，瓜子脸，正面看着镜头，JK服装，商业摄影，室外，电影级光照，半身特写，精致的淡妆，锐利的边缘。";
        String prompt = "远景镜头，唐三藏大战吉吉国王";
        
        ImageSynthesisParam param =
                ImageSynthesisParam.builder()
                        .apiKey(System.getenv("DASHSCOPE_API_KEY"))
                        .model(ImageSynthesis.Models.WANX_V1)
                        .prompt(prompt)
                        .style("<watercolor>")
                        .n(1)
                        .size("1024*1024")
                        .build();

        ImageSynthesis imageSynthesis = new ImageSynthesis();
        ImageSynthesisResult result = null;
        try {
            System.out.println("---sync call, please wait a moment----");
            result = imageSynthesis.call(param);
        } catch (ApiException | NoApiKeyException e){
            throw new RuntimeException(e.getMessage());
        }
        System.out.println(JsonUtils.toJson(result));
    }

    public static void listTask() throws ApiException, NoApiKeyException {
        ImageSynthesis is = new ImageSynthesis();
        AsyncTaskListParam param = AsyncTaskListParam.builder().build();
        ImageSynthesisListResult result = is.list(param);
        System.out.println(result);
        
        System.out.println ("过段时间拿这个taskid去获取图片："+ result.getData ().get ( 0 ).getTaskId () );
    }

    @Test
    public void start() throws ApiException, NoApiKeyException {
         basicCall();
         listTask();
    }
    
    @Test
    public void fetchTask() throws Exception {
        String taskId = "814ed7c0-82d8-4c8a-886c-e9d77a29ef19";
        ImageSynthesis is = new ImageSynthesis();
        // If set DASHSCOPE_API_KEY environment variable, apiKey can null.
        ImageSynthesisResult result = is.fetch(taskId, null);
        System.out.println(result.getOutput());
        System.out.println(result.getUsage());
        
        //获取网站上生成的图片到本地保存
        for ( Map<String, String> maps : result.getOutput().getResults () ) {
           for ( String keys : maps.keySet () ) {
               if(keys.equals ( "url" )) {
                   InputStream imageIS=new URL(maps.get ( keys )).openStream ();
                   
                   IOUtils.copy ( imageIS, new FileOutputStream("d://"+taskId+".png") );
               }
           }
        }
    }
}
