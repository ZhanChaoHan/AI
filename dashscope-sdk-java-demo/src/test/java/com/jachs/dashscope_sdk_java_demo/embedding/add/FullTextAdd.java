package com.jachs.dashscope_sdk_java_demo.embedding.add;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.alibaba.dashscope.embeddings.TextEmbedding;
import com.alibaba.dashscope.embeddings.TextEmbeddingParam;
import com.alibaba.dashscope.embeddings.TextEmbeddingResult;
import com.alibaba.dashscope.utils.JsonUtils;
import com.jachs.dashscope_sdk_java_demo.util.Utils;

import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;

/***
 * 文本向量
 * @author zhanchaohan
 */
public class FullTextAdd {
	String instruct="给出一个web查询,分析这个片段文章回答这个问题";
	List<String> texts=Arrays.asList(
			"松下茅亭五月凉，汀沙云树晚苍苍。",
			"行人无限秋风思，隔水青山似故乡。",
			"深闺秋色早，中妇倚流黄。",
			"张彄乘月影，完匹续灯光。",
			"窦女思千里，天仙妒七襄。",
			"渐老风光不著人，花溪柳陌早逢春。",
			"近来行到门前少，趁暖闲眠似病人。",
			"王守仁，字伯安，余姚人。弱冠举乡试，学大进。顾益好言兵，且善射。登弘治十二年进士。",
			"青海长云暗雪山，孤城遥望玉门关。"
			);
	
	String apiKey = "Bearer " + System.getenv("bainian_02");
	boolean delBase = false;// 是否删库
	
	String tableName="qianwen_text";
	String modelName = "text-embedding-v1";
	int dimension = 1536;
	
	@Test
	public void t1() throws Exception {
		PgVectorEmbeddingStore store = Utils.initPvDb2(delBase, dimension, tableName);
		TextEmbedding textEmbedding = new TextEmbedding();
		
		for (String text : texts) {
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
			    	
					Embedding emb = Embedding.from(Utils.DoubleToFloat(vector));
					Metadata metadata = new Metadata();
					metadata.put("text", text);
					TextSegment textSegment = TextSegment.from(text, metadata);
			
					store.add(emb, textSegment);
		}
	}
}
