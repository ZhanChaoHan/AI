package com.jachs.rag.doc.splitter;

import java.util.List;

import org.junit.jupiter.api.Test;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.splitter.DocumentBySentenceSplitter;
import dev.langchain4j.data.segment.TextSegment;

/***
‌分割依据‌:使用 NLP模型（如 OpenNLP）识别句子边界（句号、问号、感叹号等），而非简单的正则匹配。
‌适用场景‌:‌法律文书‌、‌新闻报道‌、‌学术论文‌、‌医疗记录‌等需要严格保持句子语义完整的文本。
‌语义保留度‌ ⭐⭐⭐⭐（高）。比按单词或字符分割好得多，因为保证了语法单元的完整性。
‌回退机制‌:如果单个句子长度超过 maxSegmentSize，它会回退到 DocumentByWordSplitter 进行进一步细分，确保分片不超标。
‌性能考量‌由于涉及 NLP 模型加载和计算，分割速度比简单的按字符或按行分割稍慢，但在大多数 RAG 预处理场景中可接受。
‌语言支持‌对英文支持最好；对中文支持取决于底层 NLP 库（OpenNLP 对中文支持有限，若主要处理中文，建议检查是否引入了专门的中文分句模型或使用递归分割器作为替代）。
‌最佳实践建议：‌

‌重叠设置‌：建议设置 10%-20% 的重叠，因为句子之间往往存在逻辑依赖（如“因此”、“然而”开头的句子），重叠能显著提升检索后的生成质量。
‌组合使用‌：在构建复杂 RAG 管道时，DocumentBySentenceSplitter 常作为 DocumentSplitters.recursive() 的第二或第三层级（顺序：段落 -> 句子 -> 单词），以兼顾大块语义和小块精度。
 * @author zhanchaohan
 */
public class DocumentBySentenceSplitterDemo {

    @Test
    public void t1() {
        // 1. 准备包含复杂句式的文本（如法律或技术文档）
        String text = "根据《民法典》规定，民事主体从事民事活动，应当遵循诚信原则。" +
                      "此外，当事人应当按照约定全面履行自己的义务。" +
                      "如果一方不履行合同义务或者履行合同义务不符合约定，应当承担继续履行、采取补救措施或者赔偿损失等违约责任。";

        Document document = Document.from(text);

        // 2. 初始化分割器
        // 参数1: maxSegmentSize (最大分片字符数，例如 150)
        // 参数2: maxOverlapSize (重叠字符数，例如 20，用于保持上下文连贯)
        DocumentBySentenceSplitter splitter = new DocumentBySentenceSplitter(150, 20);

        // 3. 执行分割
        List<TextSegment> segments = splitter.split(document);

        // 4. 输出结果
        for (int i = 0; i < segments.size(); i++) {
            System.out.println("--- 分片 " + (i + 1) + " ---");
            System.out.println(segments.get(i).text());
        }
    }
    
    @Test
    public void t2() {
        String text = "Artificial intelligence is transforming industries. " +
                "Machine learning models are becoming more accurate. " +
                "However, ethical considerations remain a significant challenge.";

      Document document = Document.from(text);
    
      // 1. 创建 Token 估算器
//      OpenAiTokenizer tokenizer = new OpenAiTokenizer("gpt-3.5-turbo");
    
      // 2. 初始化分割器，指定基于 Token 计数
      // 参数1: maxTokens (每个分片最大 Token 数，例如 15)
      // 参数2: maxOverlapTokens (重叠 Token 数，例如 3)
      // 参数3: tokenCountEstimator (Token 估算器)
      DocumentBySentenceSplitter splitter = new DocumentBySentenceSplitter(15, 3);
    
      // 3. 执行分割
      List<TextSegment> segments = splitter.split(document);
    
      for (int i = 0; i < segments.size(); i++) {
          System.out.printf("分片 %d: %s%n", i+1, segments.get(i).text());
      }
    }
}
