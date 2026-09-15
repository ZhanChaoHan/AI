package com.jachs.dashscope_sdk_java_demo.embedding.add;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.alibaba.dashscope.embeddings.MultiModalEmbedding;
import com.alibaba.dashscope.embeddings.MultiModalEmbeddingItemText;
import com.alibaba.dashscope.embeddings.MultiModalEmbeddingParam;
import com.alibaba.dashscope.embeddings.MultiModalEmbeddingResult;
import com.jachs.dashscope_sdk_java_demo.util.Utils;

import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;

/***
 * @author zhanchaohan
 */
public class TextAddDemo {
	String apiKey = "Bearer " + System.getenv("bainian_02");
	boolean delBase = false;// 是否删库
	
	String tableName="qianwen_text";
	String modelName = "multimodal-embedding-v1";
	int dimension = 1024;
	
	
	List<String>fileList=Arrays.asList(
			"松下茅亭五月凉，汀沙云树晚苍苍。",
			"行人无限秋风思，隔水青山似故乡。",
			"深闺秋色早，中妇倚流黄。",
			"张彄乘月影，完匹续灯光。",
			"窦女思千里，天仙妒七襄。",
			"渐老风光不著人，花溪柳陌早逢春。",
			"近来行到门前少，趁暖闲眠似病人。",
			"王守仁，字伯安，余姚人。弱冠举乡试，学大进。顾益好言兵，且善射。登弘治十二年进士。",
			"青海长云暗雪山，孤城遥望玉门关。");
	
	MultiModalEmbedding embedding = new MultiModalEmbedding();
	
	
	@Test
	public void t1() throws Exception {
		for (String file : fileList) {
			MultiModalEmbeddingItemText text = MultiModalEmbeddingItemText.builder().text(file).build();
			
			MultiModalEmbeddingParam param = MultiModalEmbeddingParam
					.builder().apiKey(apiKey)
					.model(modelName).contents(Arrays.asList(text))
					.parameters(Map.of(
							"dimension",dimension,
							"instruct","向量化文本"
							))
					.build();
			MultiModalEmbeddingResult result = embedding.call(param);
	
			System.out.print(result);
	
			PgVectorEmbeddingStore store = Utils.initPvDb2(delBase, dimension, tableName);
	
			List<Double> vector = result.getOutput().getEmbeddings().get(0).getEmbedding();
	
			Embedding emb = Embedding.from(Utils.DoubleToFloat(vector));
			Metadata metadata = new Metadata();
			metadata.put("name", file);
	
			TextSegment textSegment = TextSegment.from(file, metadata);
	
			store.add(emb, textSegment);
		}
	}
}
