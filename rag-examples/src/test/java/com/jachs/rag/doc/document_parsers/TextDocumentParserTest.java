package com.jachs.rag.doc.document_parsers;


import dev.langchain4j.data.document.BlankDocumentException;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;

class TextDocumentParserTest {

    @Test
    void should_parse_with_utf8_charset_by_default() throws FileNotFoundException {
        TextDocumentParser parser = new TextDocumentParser();
        InputStream inputStream = new FileInputStream("D://A.docx");

        Document document = parser.parse(inputStream);

        System.out.println(document.text());
    }

    @Test
    void should_parse_with_specified_charset() throws FileNotFoundException {

        TextDocumentParser parser = new TextDocumentParser(UTF_8);
        InputStream inputStream = new FileInputStream("D://A.docx");

        Document document = parser.parse(inputStream);

        System.out.println(document.text());
    }



    void should_throw_BlankDocumentException(String fileName) {

        DocumentParser parser = new TextDocumentParser();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);


    }

    @Test
    void should_wrap_input_stream_errors() {
        InputStream badStream = new InputStream() {
            @Override
            public int read() throws IOException {
                throw new IOException("test exception");
            }
        };

        TextDocumentParser parser = new TextDocumentParser();


    }
}
