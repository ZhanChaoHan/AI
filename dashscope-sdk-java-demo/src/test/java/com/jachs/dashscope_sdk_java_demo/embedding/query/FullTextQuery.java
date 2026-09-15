package com.jachs.dashscope_sdk_java_demo.embedding.query;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.alibaba.dashscope.embeddings.TextEmbedding;
import com.alibaba.dashscope.embeddings.TextEmbeddingParam;
import com.alibaba.dashscope.embeddings.TextEmbeddingResult;
import com.alibaba.dashscope.utils.JsonUtils;
import com.jachs.dashscope_sdk_java_demo.util.Utils;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;

/***
 * @author zhanchaohan
 */
public class FullTextQuery {
	String instruct="给出一个web查询,分析这个片段文章回答这个问题";
	
	String apiKey = "Bearer " + System.getenv("bainian_02");
	boolean delBase = false;// 是否删库
	
	String tableName="qianwen_text";
	String modelName = "text-embedding-v1";
	int dimension = 1536;
	
	String text="青海长云暗雪山，孤城遥望玉门关。";
	
	
	@Test
	public void t1() throws Exception {
		PgVectorEmbeddingStore store = Utils.initPvDb2(delBase, dimension, tableName);
		TextEmbedding textEmbedding = new TextEmbedding();
		
			TextEmbeddingParam param = TextEmbeddingParam
			        .builder()
			        .apiKey(apiKey)
			        .model(modelName)
			        .dimension(dimension)
			        .outputType(TextEmbeddingParam.OutputType.DENSE)
			        .instruct(instruct)
			        .texts(Arrays.asList(text)).build();
			        
			        TextEmbeddingResult result = textEmbedding.call(param);
			        System.out.println(JsonUtils.gson.toJson(result));
			        
			        List<Double> vector = result.getOutput().getEmbeddings().get(0).getEmbedding();
			    	
			        Embedding queryEmbedding = Embedding.from(Utils.DoubleToFloat(vector));
					
					EmbeddingSearchRequest request = EmbeddingSearchRequest.builder()
					        .queryEmbedding(queryEmbedding)
					        .maxResults(10)
					        .minScore(0.5)
					        .build();

					List<EmbeddingMatch<TextSegment>> matches = store.search(request).matches();
					
					Utils.printEmbeddingMatch(matches);
		}
}
