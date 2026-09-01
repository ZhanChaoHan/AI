package com.jachs.rag.chat.chat_memory;

import org.junit.jupiter.api.Test;

/***
手动维护和管理ChatMessage是很麻烦的。 因此，LangChain4j提供了ChatMemory抽象以及多种开箱即用的实现。
ChatMemory可以作为独立的低级组件使用， 或者作为高级组件（如AI服务）的一部分。
ChatMemory作为ChatMessage的容器（由List支持），具有以下额外功能：
淘汰策略
持久化
对SystemMessage的特殊处理
对工具消息的特殊处理
请注意，"记忆"和"历史"是相似但不同的概念。
历史保持用户和AI之间的所有消息完整无缺。历史是用户在UI中看到的内容。它代表实际对话内容。
记忆保存一些信息，这些信息呈现给LLM，使其表现得好像"记住"了对话。 记忆与历史有很大不同。根据使用的记忆算法，它可以以各种方式修改历史： 淘汰一些消息，总结多条消息，总结单独的消息，从消息中删除不重要的细节， 向消息中注入额外信息（例如，用于RAG）或指令（例如，用于结构化输出）等等。
LangChain4j目前只提供"记忆"，而不是"历史"。如果您需要保存完整的历史记录，请手动进行。
 * @author zhanchaohan
 */
public class Demo {

	@Test
	public void t1() {
		
	}
}
