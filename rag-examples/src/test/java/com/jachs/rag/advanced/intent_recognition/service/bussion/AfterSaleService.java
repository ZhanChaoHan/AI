package com.jachs.rag.advanced.intent_recognition.service.bussion;

import com.jachs.rag.advanced.intent_recognition.service.IntentResult.ExtractedEntities;

/***
 * @author zhanchaohan
 */
public class AfterSaleService {

	public String applyRefund(ExtractedEntities entities) {
		return "申请被拒绝"+entities.toString();
	}

	public String applyReturn(ExtractedEntities entities) {
		return "申请返回"+entities.toString();
	}

	public String applyExchange(ExtractedEntities entities) {
		return "申请更改"+entities.toString();
	}

	public String handleComplaint(String userId, ExtractedEntities entities) {
		return "处理投诉"+userId+"\t"+entities.toString();
	}

}
