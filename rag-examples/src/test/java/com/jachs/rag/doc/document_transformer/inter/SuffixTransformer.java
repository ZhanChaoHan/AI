package com.jachs.rag.doc.document_transformer.inter;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentTransformer;
import dev.langchain4j.data.document.Metadata;

/***
 * 文件后缀获取
 * @author zhanchaohan
 */
public class SuffixTransformer implements DocumentTransformer{

	@Override
	public Document transform(Document document) {
		Metadata metadata=document.metadata();
		
		String fileName=metadata.getString("file_name");
		// 提取文件类型
		String fileType = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
		
		metadata.put("suffix", fileType);
		return document;
	}

}
