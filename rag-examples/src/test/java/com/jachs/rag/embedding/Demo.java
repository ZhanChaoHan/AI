package com.jachs.rag.embedding;

import java.util.List;

import org.junit.jupiter.api.Test;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.ClassPathDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.bgesmallenv15q.BgeSmallEnV15QuantizedEmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.IngestionResult;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;

/***
 * EmbeddingStoreIngestor 表示摄取管道，负责将 Document 摄取到 EmbeddingStore 中。
 * @author zhanchaohan
 */
public class Demo {
    EmbeddingModel embeddingModel = new BgeSmallEnV15QuantizedEmbeddingModel();
    
    @Test
    public void t1() {
        EmbeddingStore<TextSegment> embeddingStore = PgVectorEmbeddingStore.builder()
                .host("localhost")
                .port(5432)
                .database("langchat")
                .user("postgres")
                .password("12345678")
                .table("document_embeddings")
                .dimension(embeddingModel.dimension())  // 384 for AllMiniLmL6V2
                .build();
        
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
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
        
        System.out.println ( ingestionResult.tokenUsage ().totalTokenCount () );//嵌入所使用的 token 数量
        
        
    }
}
