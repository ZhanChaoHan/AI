package com.jachs.rag.doc.splitter;

import java.util.List;

import org.junit.jupiter.api.Test;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.splitter.DocumentByCharacterSplitter;
import dev.langchain4j.data.segment.TextSegment;

/***
 * @author zhanchaohan
 */
public class DocumentByCharacterSplitterDemo {

    @Test
    public void t1() {
        Document document = Document.from("这里是一段很长的待分割文本，包含大量连续内容，没有明显的段落、换行等结构化分隔符，适合用按字符数硬性截断的方式完成拆分，适配后续的向量入库和RAG检索流程。");
        // 第一个参数：每个分片的最大字符数
        // 第二个参数：相邻分片之间的重叠字符数，用于保留上下文连续性
        DocumentByCharacterSplitter splitter=new DocumentByCharacterSplitter(15, 10);
        
        // 3. 执行分割，得到文本分片列表
        List<TextSegment> segments = splitter.split(document);
        // 4. 输出分割结果
        for (int i = 0; i < segments.size(); i++) {
            System.out.printf("分片 %d：%s%n", i+1, segments.get(i).text());
        }
    }
}
