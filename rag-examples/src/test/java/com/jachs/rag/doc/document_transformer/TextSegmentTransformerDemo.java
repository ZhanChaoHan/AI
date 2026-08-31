package com.jachs.rag.doc.document_transformer;

import java.util.List;

import org.junit.jupiter.api.Test;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.ClassPathDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.bgesmallenv15q.BgeSmallEnV15QuantizedEmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.IngestionResult;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;

/***
 * TextSegmentTransformer 转换 TextSegment。
 * 如果你希望在嵌入之前清理、丰富或格式化 TextSegment，这会很有用。
 * @author zhanchaohan
 */
public class TextSegmentTransformerDemo {

    @Test
    public void t1() {
        EmbeddingModel embeddingModel = new BgeSmallEnV15QuantizedEmbeddingModel();
        DocumentSplitter splitter = DocumentSplitters.recursive(100, 0);
        
        EmbeddingStore<TextSegment> embeddingStore = PgVectorEmbeddingStore.builder()
                .host("localhost")
                .port(5432)
                .database("langchat")
                .user("postgres")
                .password("12345678")
                .table("document_embeddingsA")
                .dimension(embeddingModel.dimension())  // 384 for AllMiniLmL6V2
                .build();
        
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                // adding userId metadata entry to each Document to be able to filter by it later
                .documentTransformer(document -> {
                    document.metadata().put("userId", "12345");
                    return document;
                })
                .documentSplitter(splitter)
                // adding a name of the Document to each TextSegment to improve the quality of search
                .textSegmentTransformer(textSegment -> TextSegment.from(
                        textSegment.metadata().getString("file_name") + "\n" + textSegment.text(),
                        textSegment.metadata()
                ))
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build();
        
        
        Document document1=ClassPathDocumentLoader.
                loadDocument("documents/a.txt", new TextDocumentParser());
        Document document2=ClassPathDocumentLoader.
                loadDocument("documents/b.txt", new TextDocumentParser());
        Document document3=ClassPathDocumentLoader.
                loadDocument("documents/c.txt", new TextDocumentParser());
        Document document4=ClassPathDocumentLoader.
                loadDocument("documents/d.txt", new TextDocumentParser());
        Document document5=ClassPathDocumentLoader.
                loadDocument("documents/e.txt", new TextDocumentParser());
        Document document6=ClassPathDocumentLoader.
                loadDocument("documents/aa.txt", new TextDocumentParser());
        
        ingestor.ingest(document1);
        ingestor.ingest(document2, document3);
        IngestionResult ingestionResult = ingestor.ingest(List.of(document4, document5, document6));
    }
}
