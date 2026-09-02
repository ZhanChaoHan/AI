package com.jachs.rag.doc.document_transformer;

import org.junit.jupiter.api.Test;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentTransformer;
import dev.langchain4j.data.document.transformer.jsoup.HtmlToTextDocumentTransformer;

/**
目前，开箱即用的唯一实现是langchain4j-document-transformer-jsoup模块中的HtmlToTextDocumentTransformer， 它可以从原始HTML中提取所需的文本内容和元数据条目。
由于没有一刀切的解决方案，我们建议你根据自己的独特数据实现自己的DocumentTransformer。
 * @author zhanchaohan
 */
public class HtmlToTextDocumentTransformerDemo {

    @Test
    public void t1() {
        // 1. 准备包含 HTML 标签的原始文档
        String htmlContent = "<html><body><h1>LangChain4j 简介</h1>" +
                             "<p>这是一个强大的 Java LLM 框架。</p>" +
                             "<script>alert('ignore me');</script></body></html>";
        
        Document htmlDoc = Document.from(htmlContent);

        // 2. 初始化转换器
        // 默认配置：提取文本，忽略脚本和样式
        DocumentTransformer transformer = new HtmlToTextDocumentTransformer();

        // 3. 执行转换
        Document textDoc = transformer.transform(htmlDoc);

        // 4. 输出结果
        System.out.println("原始 HTML: " + htmlDoc.text());
        System.out.println("转换后文本: " + textDoc.text());
    }
}
