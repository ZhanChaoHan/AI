package com.jachs.rag.advanced.intent_recognition.service;


/***
 * @author zhanchaohan
 */
public enum  CustomerServiceIntent {
	// 订单相关
    CANCEL_ORDER("取消订单", "用户要求取消已下单的商品"),
    CHECK_ORDER_STATUS("查询订单状态", "查询订单物流、配送、处理进度"),
    MODIFY_ADDRESS("修改收货地址", "变更订单的收货地址或联系人"),
    
    // 售后相关
    REFUND_REQUEST("申请退款", "用户要求退款或全额退回款项"),
    RETURN_GOODS("申请退货", "用户要求退回商品并退款"),
    EXCHANGE_GOODS("申请换货", "用户要求更换同类型或不同规格商品"),
    COMPLAINT("投诉建议", "对商品质量、服务态度等提出不满"),
    
    // 商品相关
    PRODUCT_INQUIRY("商品咨询", "询问商品参数、规格、使用方法"),
    STOCK_QUERY("库存查询", "询问商品是否有货、补货时间"),
    COUPON_INQUIRY("优惠券咨询", "询问优惠券使用规则、抵扣金额"),
    
    // 其他
    TECHNICAL_SUPPORT("技术支持", "网站、APP使用故障、账号问题"),
    HUMAN_SERVICE("转人工客服", "用户明确要求与人工客服对话"),
    CHAT("闲聊", "与业务无关的闲聊、问候"),
    OTHER("其他", "无法归类到以上类别的意图"),
    UNKNOWN("未知", "内容模糊无法识别意图");

    private final String name;
    private final String description;

    CustomerServiceIntent(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() { return name; }
    public String getDescription() { return description; }
}
