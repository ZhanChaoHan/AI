package com.jachs.rag.doc.splitter;

import java.util.List;

import org.junit.jupiter.api.Test;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.splitter.DocumentByLineSplitter;
import dev.langchain4j.data.segment.TextSegment;

/***
分割依据严格以换行符 \n 为边界。
‌适用场景‌:日志文件‌（每行独立事件）、‌CSV/Excel 导出文本‌（每行一条记录）、‌诗歌‌（每行一句）、‌代码‌（每行一个语句）。
‌不适用场景普通散文、新闻报道、技术文档。因为这些文本通常由长段落组成，段落内部没有换行，导致整个段落被当作一个巨大的片段，或者无法利用语义边界。
‌语义保留度‌ ⭐⭐⭐⭐（针对行级数据极高，针对段落数据极低）。
‌回退机制‌ :如果某一行的长度超过了 maxSegmentSize，DocumentByLineSplitter 通常会回退到更细粒度的分割策略（如按句子或单词）来确保分片不超标，具体行为取决于版本实现，建议监控超长行的处理结果。
‌最佳实践建议：‌
‌重叠设置‌：对于日志或代码，重叠（Overlap）通常设为 ‌0‌，因为上一行的结尾和下一行的开头通常没有语义关联。
‌混合使用‌：如果你的文档既有结构化数据又有非结构化文本，建议使用 DocumentSplitters.recursive(...)，它会自动尝试按段落、再按行、再按句子分割，兼容性更好。
@author zhanchaohan
 */
public class DocumentByLineSplitterDemo {

    
    @Test
    public void t1() {
        // 1. 准备模拟日志或多行数据
        String logText = "2023-10-01 10:00:00 INFO Server started\n" +
                         "2023-10-01 10:05:23 WARN High memory usage detected\n" +
                         "2023-10-01 10:10:45 ERROR Connection timeout to database\n" +
                         "2023-10-01 10:15:00 INFO Request processed successfully";

        Document document = Document.from(logText);

        // 2. 初始化分割器
        // 参数1: maxSegmentSize (最大分片字符数，例如 100)
        // 参数2: maxOverlapSize (重叠字符数，例如 0，日志通常不需要重叠)
        DocumentByLineSplitter splitter = new DocumentByLineSplitter(100, 0);

        // 3. 执行分割
        List<TextSegment> segments = splitter.split(document);

        // 4. 输出结果
        for (int i = 0; i < segments.size(); i++) {
            System.out.println("--- 行片段 " + (i + 1) + " ---");
            System.out.println(segments.get(i).text());
        }
    }
}
