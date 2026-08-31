package com.jachs.rag.doc.document_parsers;


import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.util.List;

import org.junit.jupiter.api.Test;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;

/**
TextDocumentParser，来自 langchain4j 模块，可以解析纯文本格式的文件（例如 TXT、HTML、MD 等）
ApachePdfBoxDocumentParser，来自 langchain4j-document-parser-apache-pdfbox 模块，可以解析 PDF 文件
ApachePoiDocumentParser，来自 langchain4j-document-parser-apache-poi 模块，可以解析 MS Office 文件格式 （例如 DOC、DOCX、PPT、PPTX、XLS、XLSX 等）
ApacheTikaDocumentParser，来自 langchain4j-document-parser-apache-tika 模块， 可以自动检测并解析几乎所有现有的文件格式
DoclingDocumentParser，来自 langchain4j-document-parser-docling 模块， 使用 Docling Java 和 Docling 来处理文档。
MarkdownDocumentParser，来自 langchain4j-document-parser-markdown 模块， 可以解析 markdown 格式的文件
YamlDocumentParser，来自 langchain4j-document-parser-yaml 模块， 可以解析 yaml 格式的文件
 * @author zhanchaohan
 */
public class DocumentParser {

    //本地磁盘根路径读取
    @Test
    public void t0() {
        //解析txt
//        Document document = FileSystemDocumentLoader.loadDocument("/a.txt", new TextDocumentParser());
//        System.out.println (document.text());
        //解析docx
//        Document document1 = FileSystemDocumentLoader.loadDocument("/A.docx", new ApachePoiDocumentParser ());
//        System.out.println (document1.text());
        //解析pdf
        Document document2 = FileSystemDocumentLoader.loadDocument("/A.pdf", new ApachePdfBoxDocumentParser ());
        System.out.println (document2.text());
        
    }
    
    
    @Test
    public void t1() {
        // Load all *.txt documents from a directory
        PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:*.txt");
        List<Document> documents = FileSystemDocumentLoader.loadDocuments("/home/langchain4j", pathMatcher, new TextDocumentParser());

        // Load all documents from a directory and its subdirectories
//        List<Document> documents = FileSystemDocumentLoader.loadDocumentsRecursively("/home/langchain4j", new TextDocumentParser());
    }
}
