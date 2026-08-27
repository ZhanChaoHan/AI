package com.jachs.rag.easy;


import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.ClassPathDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import shared.Assistant;


/***
 * @author zhanchaohan
 */
public class Demo1 {
    String apiKey = System.getenv("deepseek-key");
    
    @Test
    public void t1() {
        List<Document> documents =Arrays.asList (  
                ClassPathDocumentLoader.
                loadDocument("documents/a.txt", new TextDocumentParser()),
                ClassPathDocumentLoader.
                loadDocument("documents/b.txt", new TextDocumentParser()));
        
        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();//内存库

       
        EmbeddingStoreIngestor.ingest(documents, embeddingStore);//文档写入内存库
        
        EmbeddingStoreContentRetriever contentRetriever=EmbeddingStoreContentRetriever.from(embeddingStore);
       
        ChatLanguageModel deepSeekModel = (ChatLanguageModel) OpenAiChatModel.builder()
                .apiKey(apiKey)
                .baseUrl("https://api.deepseek.com/v1") // DeepSeek官方API端点
                .modelName("deepseek-chat") // 可选deepseek-reasoner（推理模型）
                .temperature(1.3) // DeepSeek推荐>1.0以获得更好生成效果
                .timeout(Duration.ofSeconds(60))
                .maxTokens(1000)
                .build();
        
        Assistant assistant= AiServices.builder(Assistant.class).chatMemory ( MessageWindowChatMemory.withMaxMessages(10) )
        .contentRetriever ( contentRetriever )
        .chatLanguageModel ( deepSeekModel )
        .build ();
        
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                System.out.println ("==================================================" );
                System.out.println ("User: ");
                String userQuery = scanner.nextLine();
                System.out.println ("==================================================");

                if ("exit".equalsIgnoreCase(userQuery)) {
                    break;
                }
                String agentAnswer = assistant.answer(userQuery);
                System.out.println ("==================================================");
                System.out.println ("Assistant: " + agentAnswer);
            }
        }
    }
}
