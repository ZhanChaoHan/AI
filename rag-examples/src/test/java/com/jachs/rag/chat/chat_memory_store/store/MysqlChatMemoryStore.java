package com.jachs.rag.chat.chat_memory_store.store;

import java.util.List;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;

/***
 * 需要自己实现接口
 * @author zhanchaohan
 */
public class MysqlChatMemoryStore implements ChatMemoryStore{

	@Override
	public List<ChatMessage> getMessages(Object memoryId) {
		return null;
	}

	@Override
	public void updateMessages(Object memoryId, List<ChatMessage> messages) {
		
	}

	@Override
	public void deleteMessages(Object memoryId) {
		
	}

}
