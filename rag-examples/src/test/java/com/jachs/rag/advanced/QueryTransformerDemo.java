package com.jachs.rag.advanced;

import java.util.Collection;

import org.junit.jupiter.api.Test;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.rag.query.transformer.CompressingQueryTransformer;
import dev.langchain4j.rag.query.transformer.ExpandingQueryTransformer;
import dev.langchain4j.rag.query.transformer.QueryTransformer;
import dev.langchain4j.service.AiServices;
import shared.Assistant;

/***
压缩场景示例：用户先问"介绍一下张三"，模型回答后用户接着问"他住在哪"。CompressingQueryTransformer 会结合上文，把"他住在哪"重写成"张三住在哪"，这样向量检索时才能准确命中相关文档。
扩展场景示例：用户问一个比较笼统的问题，ExpandingQueryTransformer 会让 LLM 从多个角度改写出几个子查询（比如同义词替换、拆分子问题），分别去检索，再把结果聚合，从而提高召回率。
组合使用：两者也可以链式组合（先压缩、再扩展），或者自己实现 QueryTransformer 接口，写自定义逻辑（比如关键词提取、拼写纠错等）。
如果不设置 queryTransformer，DefaultRetrievalAugmentor 会自动使用 DefaultQueryTransformer（即不转换，原样传递）。
如果你是在做多轮对话 RAG，通常建议至少用 CompressingQueryTransformer，否则指代消解不清会明显拉低检索准确率
 * @author zhanchaohan
 */
public class QueryTransformerDemo {
	OpenAiChatModel chatModel =shared.Utils.chatLanguageModel();
	
	
	@Test
	public void t1() {
		ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);//最大保留信息
		
		// 3a. 使用「压缩查询转换器」：把多轮对话压缩成一个独立、完整的查询
        QueryTransformer compressingTransformer = new CompressingQueryTransformer(chatModel);

        // 3b. 或者使用「扩展查询转换器」：把一个问题改写成多个不同角度的问题
        QueryTransformer expandingTransformer = new ExpandingQueryTransformer(chatModel);
        
        // 4. 将转换器装配进 RetrievalAugmentor
        RetrievalAugmentor retrievalAugmentor = DefaultRetrievalAugmentor.builder()
                .queryTransformer(compressingTransformer) // 或者传 expandingTransformer
//                .contentRetriever(contentRetriever)
                .build();
        
        Assistant assistant=AiServices.builder(Assistant.class)
        .chatModel(chatModel)
        .retrievalAugmentor(retrievalAugmentor)
        .chatMemory(chatMemory)
        .build();
	}
	
	
	
	//「压缩查询转换器」
	@Test
	public void t2() {
		// 1. 构造一段对话历史（模拟之前的多轮问答）
        ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);
        chatMemory.add(UserMessage.from("张三是谁？"));
        chatMemory.add(AiMessage.from("张三是一名软件工程师，目前住在北京。"));

        // 2. 构造当前这轮的新问题（指代不明，依赖上文）
        Query currentQuery = Query.from("他今年多大了？");

        // 3. 创建压缩转换器
        CompressingQueryTransformer transformer = new CompressingQueryTransformer(chatModel);

        // 4. 执行转换：transform() 需要的是带有 chatMemory 的 Query
        Query queryWithMemory = Query.from(
        		currentQuery.text(),
        		dev.langchain4j.rag.query.Metadata.from(
                        UserMessage.from("他今年多大了？"),
                        null,
                        chatMemory.messages()));

        Collection<Query> transformedQueries = transformer.transform(queryWithMemory);

        // 5. 输出转换后的独立查询
        for (Query query : transformedQueries) {
        	System.out.println("转换后的查询: " + query.text());
		}
        // 预期输出类似：转换后的查询: 张三今年多大了？
	}
	
	
	
	//「扩展查询转换器」
	@Test
	public void t3() {
		// 1. 创建扩展查询转换器，默认会生成 3 个改写版本（可通过构造函数指定数量）
        ExpandingQueryTransformer transformer = new ExpandingQueryTransformer(chatModel,3);

        // 2. 原始查询
        Query originalQuery = Query.from("公司的年假政策是什么？");

        // 3. 执行扩展
        Collection<Query> expandedQueries = transformer.transform(originalQuery);

        // 4. 输出所有扩展后的查询
        for (Query q : expandedQueries) {
        	 System.out.println("扩展查询: " + q.text());
		}
        // 预期输出类似：
        // 扩展查询: 公司的年假政策是什么？
        // 扩展查询: 员工每年可以休多少天带薪假期？
        // 扩展查询: 公司关于年度休假的规定有哪些？
	}
}
