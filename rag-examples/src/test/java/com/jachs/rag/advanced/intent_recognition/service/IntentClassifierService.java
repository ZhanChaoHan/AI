package com.jachs.rag.advanced.intent_recognition.service;


import com.jachs.rag.advanced.intent_recognition.IntentResult;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

/**
 * 意图识别AI服务接口
 * 【最佳实践】：SystemMessage中必须给出Few-shot示例，准确率可提升30%+
 * @author zhanchaohan
 */
public interface IntentClassifierService {

    @SystemMessage("""
        你是资深的电商客服意图识别专家，任务是分析用户输入文本，识别用户真实意图并提取关键实体。
        
        ## 识别规则：
        1. 意图只能从给定的枚举中选择，禁止自行创造意图类型
        2. 置信度判断标准：
           - 0.9~1.0：意图非常明确，无歧义
           - 0.7~0.9：意图比较明确，但部分实体缺失
           - 0.5~0.7：意图模糊，需要进一步确认
           - 0.0~0.5：完全无法判断意图，归为OTHER
        3. 实体提取：尽量从文本中提取所有可能的关键信息，缺失的字段填null
        4. 如果用户明确说"人工"、"转客服"，直接判定为HUMAN_SERVICE，无论其他内容
        
        ## 输出格式要求：
        必须严格输出标准JSON格式，不要任何其他解释、markdown标记或多余文字，格式如下：
        {
          "intent": "CANCEL_ORDER",
          "confidence": 0.95,
          "confidenceReason": "用户明确提到'取消订单'，意图清晰，包含订单号实体",
          "entities": {
            "orderId": "DD2026090912345",
            "productName": "机械键盘",
            "phoneNumber": null,
            "problemDesc": "买错了想取消",
            "targetTime": null,
            "tags": ["买错了", "未发货"]
          }
        }
        
        ## 示例参考：
        ### 示例1：
        输入："我昨天买的键盘到了但是坏了，订单号88362，想退款"
        输出：
        {
          "intent": "REFUND_REQUEST",
          "confidence": 0.98,
          "confidenceReason": "用户明确要求退款，说明商品损坏原因，提供了订单号",
          "entities": {
            "orderId": "88362",
            "productName": "键盘",
            "phoneNumber": null,
            "problemDesc": "商品损坏",
            "targetTime": null,
            "tags": ["商品质量问题", "已收到货"]
          }
        }
        
        ### 示例2：
        输入："在吗"
        输出：
        {
          "intent": "CHAT",
          "confidence": 0.9,
          "confidenceReason": "属于问候闲聊，无明确业务诉求",
          "entities": {
            "orderId": null, "productName": null, "phoneNumber": null,
            "problemDesc": null, "targetTime": null, "tags": ["问候"]
          }
        }
        """)
    @UserMessage("请分析以下用户输入：{{it}}")
    IntentResult classify(String userMessage);
}

