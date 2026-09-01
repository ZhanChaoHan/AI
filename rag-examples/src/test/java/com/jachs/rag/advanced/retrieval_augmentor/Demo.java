package com.jachs.rag.advanced.retrieval_augmentor;

import org.junit.jupiter.api.Test;

/***
在 LangChain4j 中，RetrievalAugmentor 是构建高级 RAG（检索增强生成）应用的核心组件。
它充当了“中央处理器”的角色，负责协调查询转换、路由、检索、内容聚合（重排序）以及上下文注入等全流程。
以下是 RetrievalAugmentor 的三个典型使用案例，从基础默认配置到高级多源重排场景。

组件	作用	常见实现类
QueryTransformer‌	优化用户查询，补充上下文	DefaultQueryTransformer, CompressingQueryTransformer
QueryRouter‌	决定查询指向哪个知识库	DefaultQueryRouter (全搜), LanguageModelQueryRouter (智能路由)
ContentRetriever‌	执行实际的向量/关键词检索	EmbeddingStoreContentRetriever, WebSearchContentRetriever
ContentAggregator‌	合并、去重、重排序检索结果	ReRankingContentAggregator (需配合 ScoringModel)
ContentInjector‌	将检索内容格式化并注入 Prompt	DefaultContentInjector
 * @author zhanchaohan
 */
public class Demo {

	@Test
	public void t1() {
		
	}
}
