package com.jachs.rag.doc.splitter;




import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.ClassPathDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentByCharacterSplitter;
import dev.langchain4j.data.document.splitter.DocumentByRegexSplitter;
import dev.langchain4j.data.segment.TextSegment;

/****
 * DocumentSplitter 文档拆分器
 * DocumentByCharacterSplitter
 * 无符号分割
 * 就是严格根据字数分隔（不推荐，会出现断句）
 * DocumentByRegexSplitter
 * 正则表达式分隔
 * 根据自定义正则‌分隔
 * DocumentByParagraphSplitter
 * 删除大段空白内容
 * 处理连续换行符（如段落分隔）（\s*(?>\R)\s*(?>\R)\s*
 * DocumentByLineSplitter
 * 删除单个换行符周围的空白， 替换一个换行
 * （\s*\R\s*）
 * ‌示例‌：
 * 输入文本：“This is line one.\n\tThis is line two.”
 * 使用 \s*\R\s* 替换为单个换行符：“This is line one.\nThis is line two.”
 * DocumentByWordSplitter
 * 删除连续的空白字符。
 * \s+
 * 示例‌
 * 输入文本：“Hello World”
 * 使用 \s+ 替换为单个空格：“Hello World”
 * DocumentBySentenceSplitter
 * 按句子分割
 * 该分割器使用Apache OpenNLP 库中的一个类，用于检测文本中的句子边界。它能够识别标点符号（如句号、问号、感叹号等）是否标记着句子的末尾，从而将一个较长的文本字符串分割成多个句子。
 * ————————————————

 * @author  zhanchaohan
 */
public class DocumentSplitterDemo {

    @Test
    public void t1(){
        Document document = ClassPathDocumentLoader.
                loadDocument("com/jachs/langchain4j/embedding_stores/pg/add/a.txt", new TextDocumentParser());
        String text = document.text();
        System.out.println(text);
    }

    @Test
    public void byLine() throws FileNotFoundException {
        TextDocumentParser parser = new TextDocumentParser(UTF_8);
        InputStream inputStream = new FileInputStream("D://A.TXT");

        Document document = parser.parse(inputStream);

        String[] lines = document.text().split("\n");
        List<TextSegment> segments = new ArrayList<>();

        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                segments.add(TextSegment.from(line));
            }
        }
        for (TextSegment segment : segments) {
            System.out.println(segment);
        }
    }

    @Test
    public void documentByCharacterSplitter() throws FileNotFoundException {
        TextDocumentParser parser = new TextDocumentParser(UTF_8);
        InputStream inputStream = new FileInputStream("D://A.TXT");

        Document document = parser.parse(inputStream);

        DocumentByCharacterSplitter splitter = new DocumentByCharacterSplitter(
                20,         // 每段最长字数
                10          // 自然语言最大重叠字数
        );
        List<TextSegment> segments = splitter.split(document);
        for (TextSegment segment : segments) {
            System.out.println(segment);
        }

    }


    @Test
    public void documentByRegexSplitter() throws FileNotFoundException {
        TextDocumentParser parser = new TextDocumentParser(UTF_8);
        InputStream inputStream = new FileInputStream("D://a.TXT");

        Document document = parser.parse(inputStream);

        DocumentByRegexSplitter splitter = new DocumentByRegexSplitter(
                "\\n\\d\\.", //匹配 换行+"1. 这样的标题"格式
                "\\n", //保留换行符作为段落连接符
                100,         // 每段最长字数
                10                              // 自然语言最大重叠字数
        );
        List<TextSegment> segments = splitter.split(document);
        for (TextSegment segment : segments) {
            System.out.println(segment);
        }
    }
}