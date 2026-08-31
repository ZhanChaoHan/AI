package com.jachs.rag.doc.splitter;

import java.util.List;

import org.junit.jupiter.api.Test;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import dev.langchain4j.data.segment.TextSegment;

/***
‌语义保留好:优先在段落边界切断，避免把完整的句子或逻辑块拆散，检索质量高于按字符或按行分割。
‌自动回退机制:如果某个段落非常长（超过 maxSegmentSize），它不会硬性截断，而是‌递归地‌尝试按句子、再按单词进行细分，确保每个分片都不超标。
‌适用场景:技术文档、博客文章、新闻报道、书籍章节等具有明显段落结构的文本。
‌不适用场景:纯日志文件（每行独立意义）、代码文件（缩进敏感）、无换行的纯流式文本。这些场景建议分别使用 DocumentByLineSplitter 或 DocumentByRegexSplitter。
‌最佳实践建议：‌
‌重叠大小 (Overlap)‌：建议设置为分片大小的 10%-20%（如 1000 字符分片配 100-200 重叠），这能有效防止关键信息在分片边界丢失。
‌分片大小 (Max Size)‌：根据你使用的 Embedding 模型和 LLM 的上下文窗口调整。通常 500-1000 字符是一个平衡检索精度和上下文丰富度的良好区间。
 * @author zhanchaohan
 */
public class DocumentByParagraphSplitterDemo {

    @Test
    public void t1() {
        // 1. 准备包含多个段落的文本
        String text = "第一段内容：LangChain4j 是一个用于 Java 的 LLM 集成框架。\n\n" +
                      "第二段内容：它提供了统一的 API，支持 OpenAI、Azure 等模型。\n\n" +
                      "第三段内容：RAG 是其核心应用场景之一，需要合理的文档分割策略。\n"+
                      "ABCS   UI876\n"+
                      "cdd";

        Document document = Document.from(text);

        // 2. 初始化分割器
        // 参数1: maxSegmentSize (最大分片字符数，例如 500)
        // 参数2: maxOverlapSize (重叠字符数，例如 50，用于保留上下文)
        DocumentByParagraphSplitter splitter = new DocumentByParagraphSplitter(500, 50);

        // 3. 执行分割
        List<TextSegment> segments = splitter.split(document);

        // 4. 输出结果
        for (int i = 0; i < segments.size(); i++) {
            System.out.println("--- 分片 " + (i + 1) + " ---");
            System.out.println(segments.get(i).text());
        }
    }
}
