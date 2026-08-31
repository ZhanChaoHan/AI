package com.jachs.rag.embedding_stores;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.junit.jupiter.api.Test;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.parser.apache.poi.ApachePoiDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.bgesmallenv15q.BgeSmallEnV15QuantizedEmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;


/***
 * @author zhanchaohan
 */
public class PgvectorAll {
    EmbeddingModel embeddingModel = new BgeSmallEnV15QuantizedEmbeddingModel();
    
    @Test
    public void t1() throws FileNotFoundException {
        DocumentParser parser = new ApachePoiDocumentParser();
        InputStream inputStream =new FileInputStream("d://a.docx");

        // Load document (PDF, TXT, etc.)
        Document document = parser.parse(inputStream);


        // Split document into smaller chunks
        // 300 tokens per chunk, 50 tokens overlap for context continuity
        DocumentSplitter splitter = DocumentSplitters.recursive(300, 50);
        // Create embedding model (384 dimensions for AllMiniLmL6V2)

        // Create pgvector embedding store
        EmbeddingStore<TextSegment> embeddingStore = PgVectorEmbeddingStore.builder()
                .host("localhost")
                .port(5432)
                .database("langchat")
                .user("postgres")
                .password("12345678")
                .table("document_embeddings")
                .dropTableFirst ( true )
                .dimension(embeddingModel.dimension())  // 384 for AllMiniLmL6V2
                .build();

        // Ingest: split document, generate embeddings, and store in pgvector
        EmbeddingStoreIngestor.builder()
                .documentSplitter(splitter)
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build()
                .ingest(document);

        System.out.println("Document ingested successfully!");
    }


}
