package com.jachs.dashscope_sdk_java_demo.ocr;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversation;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationParam;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationResult;
import com.alibaba.dashscope.aigc.multimodalconversation.OcrOptions;
import com.alibaba.dashscope.common.MultiModalMessage;
import com.alibaba.dashscope.common.Role;

/***
 * 文字提取
 * @author zhanchaohan
 */
public class Demo {
	String apiKey = "Bearer " + System.getenv("bainian_02");
	String modelName = "qwen3.5-ocr";
	String filePath = "D:\\B.png";
	
	@Test
	public void simpleMultiModalConversationCall() throws Exception{
		MultiModalConversation conv = new MultiModalConversation();
	    Map<String, Object> map = new HashMap<>();
	    map.put("image", filePath);
	    // 输入图像的最大像素阈值，超过该值图像会进行缩小，直到总像素低于max_pixels
	    map.put("max_pixels", 8388608);
	    // 输入图像的最小像素阈值，小于该值图像会进行放大，直到总像素大于min_pixels
	    map.put("min_pixels", 3072);
	    // 是否开启图像自动转正功能
	    map.put("enable_rotate", false);
	
	    // 配置内置任务
	    OcrOptions ocrOptions = OcrOptions.builder()
	            .task(OcrOptions.Task.TEXT_RECOGNITION)
	            .build();
	    MultiModalMessage userMessage = MultiModalMessage.builder().role(Role.USER.getValue())
	            .content(Arrays.asList(
	                    map
	                    )).build();
	    MultiModalConversationParam param = MultiModalConversationParam.builder()
	            .apiKey(apiKey)
	            .model(modelName)
	            .message(userMessage)
	            .ocrOptions(ocrOptions)
	            .build();
	    MultiModalConversationResult result = conv.call(param);
	    System.out.println(result.getMessage());
	    System.out.println(result.getOutput().getChoices().get(0).getMessage().getContent().get(0).get("text"));
	}

}
