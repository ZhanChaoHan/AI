package com.jachs.rag.doc.splitter;

import java.util.List;

import org.junit.jupiter.api.Test;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.splitter.DocumentByRegexSplitter;
import dev.langchain4j.data.segment.TextSegment;

/***
分割依据完全由用户提供的‌正则表达式‌决定分割点。
‌适用场景:Markdown 文档‌（按标题分割）、‌HTML/XML‌（按标签分割）、‌自定义格式日志‌、‌特定分隔符文件‌。
‌灵活性‌:⭐⭐⭐⭐⭐（最高）。可以适应任何有规律的结构化或半结构化文本。
‌回退机制:如果分割后的某个片段超过 maxSegmentSize，它通常会回退到 DocumentByParagraphSplitter 或 DocumentBySentenceSplitter 进行二次细分（具体取决于版本实现，建议监控超大片段）。
‌性能考量‌:正则编译和匹配有一定开销，但对于大多数文档预处理场景可忽略不计。复杂的正则可能影响性能。
‌注意事项‌:‌正则编写难度‌：需要具备一定的正则表达式知识。错误的正则可能导致分割失败或产生空片段。建议在上线前充分测试正则表达式。
‌最佳实践建议：‌
‌保留分隔符‌：默认情况下，正则匹配的部分可能会被丢弃或包含在下一个分片中。如果需要保留标题或标签作为上下文，需在正则中使用捕获组或在后续处理中手动添加。
‌组合使用‌：对于复杂文档，可以先用 DocumentByRegexSplitter 按大结构（如章节）粗分，再对每个大块使用 DocumentByParagraphSplitter 或 DocumentBySentenceSplitter 进行细分，实现分层分割策略。
‌测试正则‌：在使用前，务必使用在线正则测试工具验证你的正则表达式是否能正确匹配预期的分割点。
 * @author zhanchaohan
 */
public class DocumentByRegexSplitterDemo {

    
    @Test
    public void t1() {
        // 1. 准备包含 Markdown 标题的文本
        String markdownText = "# 第一章：引言\n" +
                              "这是引言部分的内容，介绍了背景知识。\n\n" +
                              "## 1.1 背景\n" +
                              "具体的背景信息在这里。\n\n" +
                              "# 第二章：核心概念\n" +
                              "这里开始讲解核心概念，内容非常长...";

        Document document = Document.from(markdownText);

        // 2. 定义正则表达式
        // 匹配以 # 开头的行（Markdown 标题），作为分割点
        // (?m) 启用多行模式，^ 匹配每行的开头
        String regex = "(?m)^#+\\s+.*$";
        
        // 3. 初始化分割器
        // 参数1: regex (分割正则)
        // 参数2:保留换行符作为段落连接符
        // 参数3: maxSegmentSize (最大分片字符数，例如 500)
        // 参数4: maxOverlapSize (重叠字符数，例如 50)
        // 注意：RegexSplitter 会在匹配到的正则位置进行切割
        DocumentByRegexSplitter splitter = new DocumentByRegexSplitter(regex,"\n", 500, 50);

        // 4. 执行分割
        List<TextSegment> segments = splitter.split(document);

        // 5. 输出结果
        for (int i = 0; i < segments.size(); i++) {
            System.out.println("--- 分片 " + (i + 1) + " ---");
            System.out.println(segments.get(i).text());
            System.out.println();
        }
    }
    
    
    @Test
    public void t2() {
        // 1. 准备由 === 分隔的文本
        String text = "第一部分：用户数据\n姓名：张三\n年龄：25\n===\n第二部分：订单数据\n订单号：1001\n金额：500\n===\n第三部分：日志\n系统启动成功";

        Document document = Document.from(text);

        // 2. 定义正则：匹配 ===
        // 使用 lookahead/lookbehind 或者简单匹配后手动处理边界，这里简单匹配 === 作为分割点
        String regex = "===";

        // 3. 初始化分割器
        DocumentByRegexSplitter splitter = new DocumentByRegexSplitter(regex,"/n", 200, 0);

        // 4. 执行分割
        List<TextSegment> segments = splitter.split(document);

        for (int i = 0; i < segments.size(); i++) {
            System.out.println("--- 分片 " + (i + 1) + " ---");
            System.out.println(segments.get(i).text().trim());
        }
    }
}
