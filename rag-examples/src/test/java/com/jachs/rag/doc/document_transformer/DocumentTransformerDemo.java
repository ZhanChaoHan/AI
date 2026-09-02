package com.jachs.rag.doc.document_transformer;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.jachs.rag.doc.document_transformer.inter.CustomHtmlCleanerTransformer;
import com.jachs.rag.doc.document_transformer.inter.LengthFilterTransformer;
import com.jachs.rag.doc.document_transformer.inter.MetadataEnricherTransformer;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentTransformer;

/***
最佳实践建议
‌顺序很重要‌：通常先做 ‌Cleaning‌（去噪），再做 ‌Enriching‌（加元数据），最后做 ‌Splitting‌（分割）。
如果在分割后再清洗，可能会导致碎片化的 HTML 标签难以处理。
‌保留 Metadata‌：在 transform 方法中创建新 Document 时，务必将原 Document 的 metadata() 传递进去，否则会导致来源、页码等关键检索信息丢失。
‌性能考量‌：如果数据量巨大，避免在 transform 中进行耗时的网络请求或复杂的 NLP 计算。对于耗时操作，建议异步处理或使用更高效的批量处理库。
‌内置转换器‌：优先检查 LangChain4j 是否已提供内置转换器（如 HtmlToTextDocumentTransformer），仅在内置功能无法满足需求时才编写自定义实现。
 * @author zhanchaohan
 */
public class DocumentTransformerDemo {

    //数据清洗
    @Test
    public void t1() {
        // 模拟一个包含 HTML 噪音的文档
        Document htmlDoc = Document.from("<html><body><h1>标题</h1><p>这是<b>重要</b>内容。</p></body></html>");

        // 实例化转换器
        DocumentTransformer transformer = new CustomHtmlCleanerTransformer();
        
        // 执行转换
        Document cleanDoc = transformer.transform(htmlDoc);

        System.out.println("原始文本: " + htmlDoc.text());
        System.out.println("清洗后: " + cleanDoc.text()); 
        // 输出: 清洗后: 标题 这是 重要 内容。
    }
    
    //数据增强
    @Test
    public void t2() {
        List<Document>docList=Arrays.asList ( 
                Document.from("一段文字"),
                Document.from("ABCD") );
        
        DocumentTransformer transformer = new MetadataEnricherTransformer("userId");
        List<Document> cleanDoc = transformer.transformAll ( docList );
        
        for ( Document document : cleanDoc ) {
            System.out.println ( document.toString () );
        }
    }
    
    //数据过滤
    @Test
    public void t3() {
        List<Document>docList=Arrays.asList ( 
                Document.from("一段文字"),
                Document.from("ABCD"),
                Document.from("*&&^%$#@$"));
        
        DocumentTransformer transformer = new LengthFilterTransformer("AB");
        List<Document> cleanDoc = transformer.transformAll ( docList );
        
        for ( Document document : cleanDoc ) {
            System.out.println ( document.toString () );
        }
    }
}
