package com.jachs.rag.advanced.intent_recognition.service;


import com.jachs.rag.advanced.intent_recognition.service.bussion.AfterSaleService;
import com.jachs.rag.advanced.intent_recognition.service.bussion.OrderService;
import com.jachs.rag.advanced.intent_recognition.service.bussion.RagQueryService;

import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;

/**
 * 意图识别服务封装（Spring环境可直接加@Service）
 */
public class IntentRecognitionService {

    private final IntentClassifierService classifier;
    private final OrderService orderService;       // 假设已有订单业务服务
    private final AfterSaleService afterSaleService; // 假设已有售后业务服务
    private final RagQueryService ragQueryService;   // 假设已有RAG问答服务

    public IntentRecognitionService(OpenAiChatModel chatModel,
                                   OrderService orderService,
                                   AfterSaleService afterSaleService,
                                   RagQueryService ragQueryService) {
        // 初始化AI服务
        this.classifier = AiServices.create(IntentClassifierService.class, chatModel);
        this.orderService = orderService;
        this.afterSaleService = afterSaleService;
        this.ragQueryService = ragQueryService;
    }

    /**
     * 处理用户消息，自动路由到对应业务
     */
    public String handleUserMessage(String userId, String userMessage) {
        try {
            // 1. 执行意图识别
            IntentResult result = classifier.classify(userMessage);
            System.out.printf("[意图识别] 用户=%s, 意图=%s, 置信度=%.2f%n",
                    userId, result.intent(), result.confidence());

            // 2. 低置信度兜底：置信度<0.7或UNKNOWN/OTHER，直接转人工或RAG兜底
            if (result.confidence() < 0.7 
                || result.intent() == CustomerServiceIntent.UNKNOWN
                || result.intent() == CustomerServiceIntent.OTHER) {
                return handleLowConfidence(result, userMessage);
            }

            // 3. 根据意图路由到对应业务处理器
            return switch (result.intent()) {
                // 订单类
                case CANCEL_ORDER -> orderService.cancelOrder(result.entities());
                case CHECK_ORDER_STATUS -> orderService.queryOrderStatus(result.entities());
                case MODIFY_ADDRESS -> orderService.modifyAddress(userId, result.entities());
                // 售后类
                case REFUND_REQUEST -> afterSaleService.applyRefund(result.entities());
                case RETURN_GOODS -> afterSaleService.applyReturn(result.entities());
                case EXCHANGE_GOODS -> afterSaleService.applyExchange(result.entities());
                case COMPLAINT -> afterSaleService.handleComplaint(userId, result.entities());
                // 商品咨询类 → 走RAG知识库回答
                case PRODUCT_INQUIRY, STOCK_QUERY, COUPON_INQUIRY -> 
                    ragQueryService.answerByKnowledgeBase(userMessage);
                // 技术支持
                case TECHNICAL_SUPPORT -> ragQueryService.answerByTechKnowledge(userMessage);
                // 转人工
                case HUMAN_SERVICE -> transferToHumanService(userId);
                // 闲聊
                case CHAT -> "你好呀！我是智能客服小助手，有什么可以帮您的吗？";
                default -> handleLowConfidence(result, userMessage);
            };

        } catch (Exception e) {
            // 4. 异常兜底：识别出错直接转RAG通用回答或提示转人工
            System.err.println("意图识别异常：" + e.getMessage());
            return "抱歉，我暂时无法理解您的问题，正在为您转接人工客服，请稍候...";
        }
    }

    /**
     * 低置信度处理：优先用RAG尝试回答，回答不了再提示转人工
     */
    private String handleLowConfidence(IntentResult result, String userMessage) {
        System.out.printf("[低置信度兜底] 置信度=%.2f, 理由=%s%n",
                result.confidence(), result.confidenceReason());
        // 先尝试用RAG知识库回答
        String ragAnswer = ragQueryService.answerByKnowledgeBase(userMessage);
        if (ragAnswer != null && !ragAnswer.contains("无法回答")) {
            return ragAnswer + "\n\n如果以上不是您想要的答案，您可以回复「人工」转接客服。";
        }
        return transferToHumanService(null);
    }

    private String transferToHumanService(String userId) {
        // 调用人工客服排队逻辑
        return "已为您转接人工客服，预计等待时间3分钟，请耐心等候~";
    }
}
