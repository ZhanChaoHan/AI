package com.jachs.rag.advanced.intent_recognition.service.bussion;

import com.jachs.rag.advanced.intent_recognition.service.IntentResult.ExtractedEntities;

/***
 * @author zhanchaohan
 */
public class OrderService {

	public String cancelOrder(ExtractedEntities entities) {
		return "取消订单"+entities.toString();
	}

	public String queryOrderStatus(ExtractedEntities entities) {
		return "询问订单状态"+entities.toString();
	}

	public String modifyAddress(String userId, ExtractedEntities entities) {
		return "修改订单地址"+userId+"\t"+entities.toString();
	}

}
