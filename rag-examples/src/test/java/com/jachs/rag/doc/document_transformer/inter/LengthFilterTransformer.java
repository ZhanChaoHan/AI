package com.jachs.rag.doc.document_transformer.inter;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentTransformer;
import java.util.List;
import java.util.stream.Collectors;

/**
内容过滤转换器：移除内容过短的文档
有时我们需要排除掉某些不符合要求的文档（例如内容过短、包含敏感词或特定格式错误）。
通过返回 null 或在 transformAll 中剔除来实现过滤。
注意：LangChain4j 的标准 transform 接口通常期望返回 Document。
如果需要过滤，通常在 transformAll 中处理更合适，或者结合后续的 DocumentFilter 使用。
以下展示如何在转换阶段标记无效文档以便后续移除。
@author zhanchaohan
 */
public class LengthFilterTransformer implements DocumentTransformer {

    private final String world;

    public LengthFilterTransformer(String world) {
        this.world = world;
    }

    @Override
    public Document transform(Document document) {
        // 单个转换通常不做过滤，只做修改。过滤逻辑建议在 transformAll 中统一处理
        return document;
    }

    @Override
    public List<Document> transformAll(List<Document> documents) {
        if (documents == null) return List.of();
        
        return documents.stream()
                .filter(doc -> !doc.text().contains ( world ))
                .collect(Collectors.toList());
    }
}