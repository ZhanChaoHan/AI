package com.jachs.rag.advanced.evaluation;

import java.util.Collection;
import org.junit.jupiter.api.Test;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.rag.query.transformer.ExpandingQueryTransformer;

/***
 * @author zhanchaohan
 */
public class Demo1 {
	// 定制中文运维场景改写Prompt，替换默认英文Prompt，避免改写漂移
    String chineseRewritePrompt = """
        你是企业运维知识库RAG系统的查询改写专家，请严格遵循以下规则处理用户查询：
        1. 结合对话历史，消除所有代词指代（如“它”、“这个”、“那个”），补全缺失的业务实体和场景
        2. 将口语化表述转换为运维专业术语，不要改变用户原始意图
        3. 生成{{n}}个不同维度的检索查询，覆盖用户问题的全部潜在信息点
        4. 每个查询独立成行，不要编号，不要多余解释，不要回答问题
        
        对话历史：
        {{chatMemory}}
        
        当前用户原始查询：{{query}}
        
        生成的3个扩展检索查询：
        """;
	@Test
	public void t1() {
		Query query=Query.from("我的名字");
		
		OpenAiChatModel chatModel=shared.Utils.chatLanguageModel();
//		ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);
//		chatMemory.add(new UserMessage("张三15岁"));
		chatModel.chat(new UserMessage("张三15岁"));
		chatModel.chat(new UserMessage("李四是女的"));
		
		
		ExpandingQueryTransformer expandingQueryTransformer=ExpandingQueryTransformer.builder()
				.chatLanguageModel(chatModel)
		.promptTemplate(PromptTemplate.from(chineseRewritePrompt))
		.n(2).build();// 生产环境设为2个扩展查询平衡召回率与延迟，测试环境可设3
		
		Collection<Query> qList=expandingQueryTransformer.transform(query);
		
		for (Query query2 : qList) {
			System.out.println(query2.text());
			System.out.println("----------------------");
		}
	}
}
