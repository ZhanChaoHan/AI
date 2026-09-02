package com.jachs.rag.advanced.query_router;

import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.query.router.DefaultQueryRouter;
import dev.langchain4j.rag.query.router.QueryRouter;

/***
案例三：带降级策略的路由 (Fallback)
在某些生产环境中，如果主检索源失败或置信度低，可以配置回退机制。
虽然 LanguageModelQueryRouter 本身主要做意图识别，但可以在构建 DefaultRetrievalAugmentor 时结合多个 Router 或自定义逻辑。
以下是一个简化的基于 Builder 的配置思路，展示如何组合不同的检索能力：
 * @author zhanchaohan
 */
public class DemoA3 {

	public static QueryRouter createFallbackRouter(ContentRetriever primaryRetriever, 
            ContentRetriever fallbackRetriever) {
		// 注意：LangChain4j 的标准 DefaultQueryRouter 是并行发送。
		// 若需严格的“主失败则次”逻辑，通常需要在 ContentAggregator 层处理，
		// 或者自定义 QueryRouter 实现。
		
		// 这里展示一种常见的混合策略：同时查询内部库和外部搜索，
		// 依靠后续的 Aggregator (如 ReRanker) 来筛选高质量结果。
		return new DefaultQueryRouter(primaryRetriever, fallbackRetriever);
	}
	
	
}
