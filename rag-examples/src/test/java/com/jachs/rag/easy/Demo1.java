package com.jachs.rag.easy;


import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.ClassPathDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.bgesmallenv15q.BgeSmallEnV15QuantizedEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
import shared.Assistant;


/***
 * @author zhanchaohan
 */
public class Demo1 {
    String apiKey = System.getenv("deepseek-key");
    EmbeddingModel embeddingModel = new BgeSmallEnV15QuantizedEmbeddingModel();
    
    
    @Test
    public void t1() {
        List<Document> documents =Arrays.asList (  
                ClassPathDocumentLoader.
                loadDocument("documents/a.txt", new TextDocumentParser()),
                ClassPathDocumentLoader.
                loadDocument("documents/b.txt", new TextDocumentParser()));
        
        PgVectorEmbeddingStore embeddingStore =shared.Utils.initPvDb();
       
        EmbeddingStoreIngestor.ingest(documents, embeddingStore);//文档写入内存库
        
        EmbeddingStoreContentRetriever contentRetriever=EmbeddingStoreContentRetriever.from(embeddingStore);
       
        ChatLanguageModel deepSeekModel = (ChatLanguageModel) OpenAiChatModel.builder()
                .apiKey(apiKey)
                .baseUrl("https://api.deepseek.com/v1")
                .modelName("deepseek-v4-flash1231412") // 可选deepseek-reasoner（推理模型）
                .temperature(1.3) // DeepSeek推荐>1.0以获得更好生成效果
                .timeout(Duration.ofSeconds(60))
                .maxTokens(1000)
                .build();
        
        Assistant assistant= AiServices.builder(Assistant.class).chatMemory ( MessageWindowChatMemory.withMaxMessages(10) )
        .contentRetriever ( contentRetriever )
        .chatLanguageModel ( deepSeekModel ).build ();
        
        shared.Utils.startConversationWith(assistant);
    }
}
