package com.jachs.tongyi.free;

import com.alibaba.dashscope.aigc.multimodalconversation.*;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.exception.UploadFileException;
import com.alibaba.dashscope.utils.JsonUtils;

import java.util.Arrays;

public class MultiModalConversationUsage {

    private static final String modelName = "qwen-vl-plus";

    // 若没有配置环境变量，请用百炼API Key将下行替换为：api_key="sk-xxx"
    public static String apiKey = System.getenv("DASHSCOPE_API_KEY");

    public static void simpleMultiModalConversationCall() throws ApiException, NoApiKeyException, UploadFileException {
        MultiModalConversation conv = new MultiModalConversation();
        MultiModalMessageItemText systemText = new MultiModalMessageItemText("You are a helpful assistant.");
        MultiModalConversationMessage systemMessage = MultiModalConversationMessage.builder()
                .role(Role.SYSTEM.getValue()).content(Arrays.asList(systemText)).build();
        MultiModalMessageItemImage userImage = new MultiModalMessageItemImage(
                "oss://dashscope-instant/ed977b897d2a9170f016076c6557f557/2025-10-31/d74d4dd6-82ce-4a84-97e5-5610b0e74e5a/D.jpg");
        MultiModalMessageItemText userText = new MultiModalMessageItemText("这是什么");
        MultiModalConversationMessage userMessage =
                MultiModalConversationMessage.builder().role(Role.USER.getValue())
                        .content(Arrays.asList(userImage, userText)).build();
        MultiModalConversationParam param = MultiModalConversationParam.builder()
                .model(MultiModalConversationUsage.modelName)
                .apiKey(apiKey)
                .message(systemMessage)
//                .incrementalOutput(true)
                .message(userMessage).build();
        MultiModalConversationResult result = conv.call(param);
        System.out.print(JsonUtils.toJson(result));

    }

    public static void main(String[] args) {
        try {
            simpleMultiModalConversationCall();
        } catch (ApiException | NoApiKeyException | UploadFileException /*| IOException*/ e) {
            System.out.println(e.getMessage());
        }
        System.exit(0);
    }

}
