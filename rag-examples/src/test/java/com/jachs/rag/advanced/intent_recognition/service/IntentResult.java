package com.jachs.rag.advanced.intent_recognition.service;


import java.util.List;

/**
 * 意图识别结果实体
 * @author zhanchaohan
 */
public record IntentResult(
    // 识别到的意图枚举值
    CustomerServiceIntent intent,
    // 意图置信度（0.0~1.0，低于0.7建议转人工兜底）
    Double confidence,
    // 置信度判断理由（便于排查问题）
    String confidenceReason,
    // 从用户问题中提取的关键实体信息
    ExtractedEntities entities
) {
    /**
     * 提取的业务实体信息（电商场景示例，可按需扩展）
     */
    public record ExtractedEntities(
        String orderId,         // 订单号
        String productName,     // 商品名称
        String phoneNumber,     // 手机号
        String problemDesc,     // 问题描述
        String targetTime,      // 期望处理时间
        List<String> tags       // 其他关键词标签
    ) {}
}
