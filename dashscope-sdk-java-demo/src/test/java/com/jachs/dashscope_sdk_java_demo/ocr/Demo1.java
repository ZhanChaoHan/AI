package com.jachs.dashscope_sdk_java_demo.ocr;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversation;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationParam;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationResult;
import com.alibaba.dashscope.common.MultiModalMessage;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.exception.UploadFileException;

import io.reactivex.Flowable;

/***
 * 图片总结
 * @author zhanchaohan
 */
public class Demo1 {
	// 以下为北京地域 base_url，若使用弗吉尼亚地域模型，需要将base_url换成 https://{WorkspaceId}.us-east-1.maas.aliyuncs.com/api/v1
    // 以下为华北2（北京）地域的URL，各地域的URL不同。
//    static {Constants.baseHttpApiUrl="https://{WorkspaceId}.cn-beijing.maas.aliyuncs.com/api/v1";}
	String apiKey = "Bearer " + System.getenv("bainian_02");
	String modelName = "qwen3.5-plus";
	
    private static StringBuilder reasoningContent = new StringBuilder();
    private static StringBuilder finalContent = new StringBuilder();
    private static boolean isFirstPrint = true;
    
    
	private static void handleGenerationResult(MultiModalConversationResult message) {
        String re = message.getOutput().getChoices().get(0).getMessage().getReasoningContent();
        String reasoning = Objects.isNull(re)?"":re; // 默认值

        List<Map<String, Object>> content = message.getOutput().getChoices().get(0).getMessage().getContent();
        if (!reasoning.isEmpty()) {
            reasoningContent.append(reasoning);
            if (isFirstPrint) {
                System.out.println("====================思考过程====================");
                isFirstPrint = false;
            }
            System.out.print(reasoning);
        }

        if (Objects.nonNull(content) && !content.isEmpty()) {
            Object text = content.get(0).get("text");
            finalContent.append(content.get(0).get("text"));
            if (!isFirstPrint) {
                System.out.println("\n====================完整回复====================");
                isFirstPrint = true;
            }
            System.out.print(text);
        }
    }
    public  MultiModalConversationParam buildMultiModalConversationParam(MultiModalMessage Msg)  {
        return MultiModalConversationParam.builder()
                .apiKey(apiKey)
                .model(modelName)
                .messages(Arrays.asList(Msg))
                .enableThinking(false)
                .incrementalOutput(true)
                .build();
    }

    public  void streamCallWithMessage(MultiModalConversation conv, MultiModalMessage Msg)
            throws NoApiKeyException, ApiException, InputRequiredException, UploadFileException {
        MultiModalConversationParam param = buildMultiModalConversationParam(Msg);
        Flowable<MultiModalConversationResult> result = conv.streamCall(param);
        result.blockingForEach(message -> {
            handleGenerationResult(message);
        });
    }
    
    
	@Test
	public void t2() throws Exception {
		byte[] fileBytes = Files.readAllBytes(Paths.get("D:\\image\\EE.png"));
		String base64Img = Base64.encodeBase64String(fileBytes);
		String fullBase64Url = "data:image/jpeg;base64," + base64Img;
		
		
		 MultiModalConversation conv = new MultiModalConversation();
         MultiModalMessage userMsg = MultiModalMessage.builder()
                 .role(Role.USER.getValue())
                 .content(Arrays.asList(Collections.singletonMap("image", fullBase64Url),
                         Collections.singletonMap("text", "总结这张图片不超过30个字")))
                 .build();
         streamCallWithMessage(conv, userMsg);
	}
}
