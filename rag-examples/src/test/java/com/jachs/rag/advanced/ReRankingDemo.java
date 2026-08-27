package com.jachs.rag.advanced;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

import dev.langchain4j.community.model.qianfan.QianfanEmbeddingModel;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.ClassPathDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.cohere.CohereScoringModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.bgesmallenv15q.BgeSmallEnV15QuantizedEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.aggregator.ContentAggregator;
import dev.langchain4j.rag.content.aggregator.ReRankingContentAggregator;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import shared.Assistant;

/***
 * @author zhanchaohan
 * @see https://cloud.baidu.com/doc/qianfan-docs/s/em95lyyjw
 * @see https://docs.langchain4j.info/integrations/embedding-models/qianfan
 * @see https://github.com/langchain4j/langchain4j-community/blob/main/models/langchain4j-community-qianfan/src/test/java/dev/langchain4j/community/model/qianfan/QianfanEmbeddingModelIT.java
 */
public class ReRankingDemo {
    String qianwen_apiKey = System.getenv("qianwen-apikey");
    String apiKey = System.getenv("deepseek-key");
    
    
    @Test
    public void t1() {
        List<Document> documents =Arrays.asList (  
                ClassPathDocumentLoader.
                loadDocument("documents/aa.txt", new TextDocumentParser()),
                ClassPathDocumentLoader.
                loadDocument("documents/bb.txt", new TextDocumentParser()));
        
        EmbeddingModel embeddingModel = new BgeSmallEnV15QuantizedEmbeddingModel();
        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
        
        
        DocumentSplitter splitter = DocumentSplitters.recursive(100, 0);
        
        for ( Document doc : documents ) {
            List<TextSegment> segments = splitter.split(doc);//切片
            List<Embedding> embeddings = embeddingModel.embedAll(segments).content();//向量化
            embeddingStore.addAll(embeddings, segments);//入库
        }
        
        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(20)
                .build();//返回20条
        
        List<Content> cList=contentRetriever.retrieve ( Query.from ( "噫吁嚱，危乎高哉！" ) );
        
        System.out.println ( "数量:"+cList.size ()+"个" );
        cList.forEach ( a->{
            System.out.println ( a.toString () );
            System.out.println ( "\n" );
        });
        
//        QianfanEmbeddingModel scoringModel = CohereScoringModel.builder()
//                .apiKey(qianwen_apiKey)
//                .modelName("bce-reranker-base")
//                .build();//调用千问的重排序模型
        
        QianfanEmbeddingModel model = QianfanEmbeddingModel.builder()
                .user("111")
                .apiKey(qianwen_apiKey)
//                .secretKey(secretKey)
                .endpoint("embedding-v1")
                .logRequests(true)
                .logResponses(true)
                .build();
        
        ContentAggregator contentAggregator = ReRankingContentAggregator.builder()
                .scoringModel ( model )
                .minScore(0.8) 
                .build();//精排后只要大于0.8数据
        
        RetrievalAugmentor retrievalAugmentor = DefaultRetrievalAugmentor.builder()
                .contentRetriever(contentRetriever)
                .contentAggregator(contentAggregator)
                .build();
        
        
        ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);//最大保留信息
        
        ChatLanguageModel deepSeekModel = (ChatLanguageModel) OpenAiChatModel.builder()
                .apiKey(apiKey)
                .baseUrl("https://api.deepseek.com/v1") // DeepSeek官方API端点
                .modelName("deepseek-chat") // 可选deepseek-reasoner（推理模型）
                .temperature(1.3) // DeepSeek推荐>1.0以获得更好生成效果
                .timeout(Duration.ofSeconds(60))
                .maxTokens(1000)
                .build();//封装对话大模型对象
        
        Assistant assistant=AiServices.builder(Assistant.class)
        .chatLanguageModel(deepSeekModel)
        .retrievalAugmentor(retrievalAugmentor)
        .chatMemory(chatMemory)
        .build();
        
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
