package com.jachs.tongyi.audio_models;

import java.util.Arrays;
import java.util.HashMap;

import org.junit.jupiter.api.Test;

import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversation;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationParam;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationResult;
import com.alibaba.dashscope.common.MultiModalMessage;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.exception.UploadFileException;
import com.alibaba.dashscope.utils.JsonUtils;

/***
 * 語音提取文字從本地文件
 * @author zhanchaohan
 * @see https://bailian.console.aliyun.com/?spm=5176.29597918.J_SEsSjsNv72yRuRFS2VknO.2.1e8c7ca0aSTpgK#/model-market/detail/qwen-audio-turbo?tabKey=sdk
 */
public class Dts2LocalFile {
    
    public static void callWithLocalFile()
            throws ApiException, NoApiKeyException, UploadFileException {
        // 请用您本地文件的实际路径替换掉ABSOLUTE_PATH/welcome.mp3
        // 当前测试系统为macOS。如果您使用Windows系统，请用"file:///ABSOLUTE_PATH/welcome.mp3"代替。
        String localFilePath = "file:///d:/output.mp3";
        MultiModalConversation conv = new MultiModalConversation();
        MultiModalMessage userMessage = MultiModalMessage.builder().role(Role.USER.getValue())
                .content(Arrays.asList(new HashMap<String, Object>(){{put("audio", localFilePath);}},
                        new HashMap<String, Object>(){{put("text", "音频里在说什么?");}}))
                .build();
        MultiModalConversationParam param = MultiModalConversationParam.builder()
                .model("qwen-audio-turbo-latest")
                .message(userMessage)
                .build();
        MultiModalConversationResult result = conv.call(param);
        System.out.println(JsonUtils.toJson(result));
    }


    @Test
    public void t1() {
        try {
            callWithLocalFile();
        } catch (ApiException | NoApiKeyException | UploadFileException e) {
            System.out.println(e.getMessage());
        }
        System.exit(0);
    }
}
