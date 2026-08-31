package com.jachs.rag.embedding_stores.add;

import java.util.List;

import org.junit.jupiter.api.Test;

import dev.langchain4j.data.document.loader.ClassPathDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.bgesmallenv15q.BgeSmallEnV15QuantizedEmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.IngestionResult;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

/***
嵌入存储摄取器
mbeddingStoreIngestor 表示摄取管道，负责将 Document 摄取到 EmbeddingStore 中。
在最简单的配置中，EmbeddingStoreIngestor 使用指定的 EmbeddingModel 对提供的 Document 进行嵌入， 
并将它们及其 Embedding 存储到指定的 EmbeddingStore 中：
 * @author zhanchaohan
 */
public class EmbeddingStoreIngestorDemo {

    @Test
    public void t1() {
        EmbeddingModel model = new BgeSmallEnV15QuantizedEmbeddingModel();//自带模型
        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();//内存库
        
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .embeddingModel(model)
                .embeddingStore(embeddingStore)
                .build();

        ingestor.ingest(ClassPathDocumentLoader.
                loadDocument("documents/a.txt", new TextDocumentParser()));
        ingestor.ingest(ClassPathDocumentLoader.
                loadDocument("documents/b.txt", new TextDocumentParser()), 
                ClassPathDocumentLoader.
                loadDocument("documents/c.txt", new TextDocumentParser()));
        
        IngestionResult ingestionResult = ingestor.ingest(List.of(ClassPathDocumentLoader.
                loadDocument("documents/d.txt", new TextDocumentParser()), 
                ClassPathDocumentLoader.
                loadDocument("documents/e.txt", new TextDocumentParser())));
        
       System.out.println (  ingestionResult.tokenUsage ().inputTokenCount () );
        
    }
}
