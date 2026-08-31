package com.jachs.rag.doc.splitter;

import java.util.List;

import org.junit.jupiter.api.Test;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.splitter.DocumentByWordSplitter;
import dev.langchain4j.data.segment.TextSegment;

/***
分割依据‌:以空白字符（空格、制表符、换行符等 \s+）为边界。多个连续空白被视为一个分隔符。
‌适用场景‌:‌英文文本‌、代码注释、以及其他以空格自然分词的语言。常作为其他分割器（如按句子分割）的‌回退策略‌。
‌不适用场景:‌中文、日文、韩文‌等无空格分隔的语言。在这些语言中，整个句子或段落会被视为一个“单词”，导致分割失效或产生超大分片。
‌语义保留度‌ ⭐⭐⭐（中等）。比按字符分割好，因为不会切断单词；但比按句子或段落分割差，因为可能切断完整的句子结构。
‌回退机制:如果单个单词长度超过 maxSegmentSize，它会使用默认的 DocumentByCharacterSplitter 进行硬性截断，确保分片不超标。
‌最佳实践建议：‌

‌中文处理‌：如果你的主要数据是中文，‌不要直接使用‌ DocumentByWordSplitter。建议使用 DocumentByParagraphSplitter 或 DocumentBySentenceSplitter，或者使用支持中文分词的自定义分割器。
‌回退策略‌：在构建递归分割器（DocumentSplitters.recursive）时，DocumentByWordSplitter 通常被用作最后一道防线之前的层级（顺序通常是：段落 -> 句子 -> 单词 -> 字符）。
‌重叠设置‌：建议设置一定的重叠（Overlap），因为按单词分割可能会在关键连接词处切断，重叠有助于保持上下文的连贯性。
 * @author zhanchaohan
 */
public class DocumentByWordSplitterDemo {

    @Test
    public void t1() {
        // 1. 准备英文文本
        String text = "LangChain4j is a Java library for integrating LLMs. " +
                      "It provides a unified API for various models. " +
                      "You can use it to build RAG applications easily.";

        Document document = Document.from(text);

        // 2. 初始化分割器
        // 参数1: maxSegmentSize (最大分片字符数，例如 50)
        // 参数2: maxOverlapSize (重叠字符数，例如 10)
        // 注意：这里的 size 默认指字符数，也可以配置为 Token 数
        DocumentByWordSplitter splitter = new DocumentByWordSplitter(50, 10);

        // 3. 执行分割
        List<TextSegment> segments = splitter.split(document);

        // 4. 输出结果
        for (int i = 0; i < segments.size(); i++) {
            System.out.println("--- 分片 " + (i + 1) + " ---");
            System.out.println(segments.get(i).text());
        }
    }
}
