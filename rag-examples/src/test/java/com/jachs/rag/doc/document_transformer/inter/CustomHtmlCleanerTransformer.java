package com.jachs.rag.doc.document_transformer.inter;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentTransformer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 自定义文档转换器：去除 HTML 标签并清理多余空白
 */
public class CustomHtmlCleanerTransformer implements DocumentTransformer {

    @Override
    public Document transform(Document document) {
        if (document == null || document.text() == null) {
            return document;
        }
        
        String originalText = document.text();
        // 1. 去除 HTML 标签
        String cleanText = originalText.replaceAll("<[^>]+>", "");
        // 2. 将多个连续空白字符替换为单个空格，并去除首尾空白
        cleanText = cleanText.replaceAll("\\s+", " ").trim();
        
        // 3. 返回新的 Document 对象，保留原有的 Metadata
        return Document.from(cleanText, document.metadata());
    }

    @Override
    public List<Document> transformAll(List<Document> documents) {
        if (documents == null) {
            return new ArrayList<>();
        }
        // 使用 Stream 并行处理可能提升大数据量下的性能
        return documents.stream()
                .map(this::transform)
                .collect(Collectors.toList());
    }
}
