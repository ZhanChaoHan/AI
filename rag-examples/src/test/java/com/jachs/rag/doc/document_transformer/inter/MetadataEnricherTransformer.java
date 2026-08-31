package com.jachs.rag.doc.document_transformer.inter;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentTransformer;
import dev.langchain4j.data.document.Metadata;

/**
 * 元数据增强转换器：为文档添加固定的来源标识和预处理标签
 */
public class MetadataEnricherTransformer implements DocumentTransformer {

    private final String sourceSystem;

    public MetadataEnricherTransformer(String sourceSystem) {
        this.sourceSystem = sourceSystem;
    }

    @Override
    public Document transform(Document document) {
        // 1. 获取现有元数据
        Metadata metadata = document.metadata();
        
        // 2. 添加新的元数据字段
        metadata.put("source_system", sourceSystem);
        metadata.put("processed_at", System.currentTimeMillis());
        
        // 3. 可选：如果文档有标题，将其拼接到文本开头，确保分割后每个片段都携带标题信息
        String title = metadata.getString("title");
        if (title != null && !title.isEmpty()) {
            String newText = "文档标题: " + title + "\n\n" + document.text();
            return Document.from(newText, metadata);
        }
        
        return Document.from(document.text(), metadata);
    }
}
