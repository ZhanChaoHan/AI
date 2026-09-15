package com.jachs.dashscope_sdk_java_demo.embedding.add;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.alibaba.dashscope.embeddings.MultiModalEmbedding;
import com.alibaba.dashscope.embeddings.MultiModalEmbeddingItemImage;
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
public class MultiModalEmbeddingDemo1 {
	String apiKey = "Bearer " + System.getenv("bainian_02");
	String modelName = "qwen2.5-vl-embedding";
	String tableName="qianwen_a";
	boolean delBase = false;// 是否删库
	int dimension = 1024;

	
	Map<String,String>maps=Map.of(
			"棕背伯劳栖于绿叶枝头，黑头白喉橙腹，姿态优雅。","D:\\image\\A.jpg",
			"月夜海滩，星光点点，小屋灯火温暖，两人漫步，梦幻宁静。","D:\\image\\B.jpeg",
			"秋日山景，层林尽染，红叶与蓝天相映成趣。","D:\\image\\C.jpeg",
			"夕阳湖畔，红玫瑰盛放，游船点点，光影交织如诗如画。","D:\\image\\D.jpg",
			"LangChain4j分类教程页面，介绍Java中实现文本分类的方法。","D:\\image\\AA.png",
			"ASF项目目录页面，提供按名称、委员会等分类的项目列表。","D:\\image\\BB.png",
			"阿里云百炼平台通用文本向量API文档页面，含同步与批处理接口详情。","D:\\image\\CC.png",
			"声音复刻API参考文档，介绍参数接口及域名迁移建议。","D:\\image\\DD.png",
			"阿里云官网底部导航，含产品、服务、权益及联系方式等信息。","D:\\image\\EE.png"
			);
	
	@Test
	public void t1() throws Exception {
		MultiModalEmbedding embedding = new MultiModalEmbedding();
		PgVectorEmbeddingStore store = Utils.initPvDb2(delBase, dimension, tableName);
		
		for (String key : maps.keySet()) {
			MultiModalEmbeddingItemText text = MultiModalEmbeddingItemText.builder().text(key).build();
			MultiModalEmbeddingItemImage image = new MultiModalEmbeddingItemImage(maps.get(key));
			MultiModalEmbeddingParam param = MultiModalEmbeddingParam
					.builder().apiKey(apiKey)
					.model(modelName).contents(Arrays.asList(text, image))
					.parameters(Map.of(
							"dimension",dimension,
							"enable_fusion",true
							))
					.build();
			MultiModalEmbeddingResult result = embedding.call(param);
	
			System.out.print(result);
			List<Double> vector = result.getOutput().getEmbeddings().get(0).getEmbedding();
	
			Embedding emb = Embedding.from(Utils.DoubleToFloat(vector));
			Metadata metadata=Metadata.from(
					Map.of("path",maps.get(key),
					"info",key));
			
			TextSegment textSegment = TextSegment.from(key, metadata);
	
			store.add(emb, textSegment);
		}
	}

}
