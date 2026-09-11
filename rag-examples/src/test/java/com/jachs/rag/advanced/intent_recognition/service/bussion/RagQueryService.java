package com.jachs.rag.advanced.intent_recognition.service.bussion;


/***
 * @author zhanchaohan
 */
public class RagQueryService {

	public String answerByKnowledgeBase(String userMessage) {
		return "知识库回答："+userMessage;
	}

	public String answerByTechKnowledge(String userMessage) {
		return "知识库问答："+userMessage;
	}

}
