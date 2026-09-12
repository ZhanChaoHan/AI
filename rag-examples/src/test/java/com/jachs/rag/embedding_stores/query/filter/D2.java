package com.jachs.rag.embedding_stores.query.filter;

import org.junit.jupiter.api.Test;

import dev.langchain4j.store.embedding.filter.MetadataFilterBuilder;

/***
 * MetadataFilterBuilder 生成filter工具类
 * @author zhanchaohan
 */
public class D2 {

	@Test
	public void t2() {
		MetadataFilterBuilder.metadataKey("key").isBetween(12, 22);
		MetadataFilterBuilder.metadataKey("").isEqualTo("")
		.and(MetadataFilterBuilder.metadataKey("").isEqualTo(""));
	}
	
	
	public void t1() {
		
	}
}
