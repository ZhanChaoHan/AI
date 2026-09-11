package com.jachs.rag.advanced.intent_recognition;

import org.junit.jupiter.api.Test;

import com.jachs.rag.advanced.intent_recognition.service.IntentRecognitionService;
import com.jachs.rag.advanced.intent_recognition.service.bussion.AfterSaleService;
import com.jachs.rag.advanced.intent_recognition.service.bussion.OrderService;
import com.jachs.rag.advanced.intent_recognition.service.bussion.RagQueryService;

import dev.langchain4j.model.openai.OpenAiChatModel;

/****
 * @author zhanchaohan
 */
public class D1 {

	@Test
	public void t1() {
		String[] testCases = {
	            "我要取消订单DD20260909123，买错了还没发货",
	            "订单88362什么时候能到？",
	            "昨天买的键盘坏了，想退款",
	            "你们这个无线鼠标怎么连蓝牙啊？",
	            "人工客服",
	            "今天天气怎么样",
	            "啊？这啥啊"
	        };
		  // 2. 初始化业务服务（此处用mock实现演示）
        OrderService orderService = new OrderService();
        AfterSaleService afterSaleService = new AfterSaleService();
        RagQueryService ragQueryService = new RagQueryService();
        
        
        OpenAiChatModel chatModel=shared.Utils.chatLanguageModel();
        
		IntentRecognitionService service = new IntentRecognitionService(
                chatModel, orderService, afterSaleService, ragQueryService);
	        // 5. 执行测试
	        for (String testCase : testCases) {
	            System.out.println("\n==============================");
	            System.out.println("用户输入：" + testCase);
	            String response = service.handleUserMessage("user001", testCase);
	            System.out.println("客服回复：" + response);
	        }
	}
}
