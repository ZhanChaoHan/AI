package com.jachs.rag.advanced;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.ClassPathDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.cohere.CohereScoringModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.bgesmallenv15q.BgeSmallEnV15QuantizedEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.scoring.ScoringModel;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.rag.query.Query;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
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
    
    
    static final  String query="噫吁嚱，危乎高哉！";
    
    @Test
    public void t1() {
        List<Document> documents =Arrays.asList (  
                ClassPathDocumentLoader.
                loadDocument("documents/aa.txt", new TextDocumentParser()),
                ClassPathDocumentLoader.
                loadDocument("documents/bb.txt", new TextDocumentParser()));
        
        EmbeddingModel embeddingModel = new BgeSmallEnV15QuantizedEmbeddingModel();
        PgVectorEmbeddingStore embeddingStore =shared.Utils.initPvDb(true);
        
        
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
                .build();//粗排返回20条
        
        List<Content> cList=contentRetriever.retrieve ( Query.from ( query) );
        
        System.out.println ( "数量:"+cList.size ()+"个" );
        cList.forEach ( a->{
            System.out.println ( a.toString () );
            System.out.println ( "\n" );
        });
        
        
        /***
			目前Langchain4j默认支持接入以下模型或者平台：
			In-process (ONNX)：本地的Scoring (Reranking) Models，加载本地模型。
			Cohere：Cohere的一个相关性评分模型
			Jina：Jina的一个相关性评分模型
			Google Cloud Vertex AI Ranking API：谷歌的一个相关性评分模型
			Voyage AI：MongoDB旗下的一个嵌入模型，可以用于相关性评分
			Xinference：是一个开源平台，用于简化各种 AI 模型的运行和集成，可以将评分大模型放入该平台运行。
         */
//        ScoringModel jinaScoringModel = JinaScoringModel.builder()
//                .apiKey("jina_f04724edb23f4d3f96ccff305342ef91t9IZcry7kzrzHL9gB6b-ucgJot-N")
//                .modelName("jina-reranker-v3")
//                .build();
        
        
        //https://dashboard.voyageai.com/organization/usage?tab=free-token
        ScoringModel scoringModel = CohereScoringModel.builder()
                .apiKey("pa-j9f3YJEglBmxbBoc8qkwgnd7o4tH1NWRYq__qrPODzN")
                .modelName("rerank-multilingual-v3.0")
                .build();
        
        /***
         * 要安装
         * https://inference.readthedocs.io/zh-cn/latest/models/model_abilities/rerank.html
         */
//        ScoringModel model = XinferenceScoringModel.builder()
//                .baseUrl(baseUrl())
//                .apiKey(apiKey())
//                .modelName(modelName())
//                .timeout(Duration.ofSeconds(60))
//                .maxRetries(1)
//                .logRequests(true)
//                .logResponses(true)
//                .build();
        
        //要安装
//        ScoringModel scoringModel =scoringModel = XinferenceScoringModel.builder()
//                .baseUrl("http://localhost:9997") // Xinference 默认端口
//                .apiKey(apiKey)
//                .build();
        
//        String modelPath = "/path/to/model_quantized.onnx";
//        String tokenizerPath = "/path/to/tokenizer.json";
//        
//        ScoringModel scoringModel = new OnnxScoringModel(
//                modelPath, 
//                new SessionOptions(), // OrtSession.SessionOptions (可选，用于配置GPU等)
//                tokenizerPath, 
//                512,  // maxSequenceLength
//                false // useCuda (是否使用GPU)
//            );
        


        
        List<TextSegment> candidates = cList.stream()
                .map(Content::textSegment)
                .toList();
        
        
        // 批量打分
        List<Double> scores = scoringModel.scoreAll(candidates,query).content();

        // 按分数降序排列，过滤 minScore < 0.1 的结果，取 Top-3
        List<String> rerankResults = IntStream.range(0, candidates.size())
                .boxed()
                .filter(i -> scores.get(i) >= 0.1)          // minScore 过滤
                .sorted((a, b) -> Double.compare(scores.get(b), scores.get(a)))
                .limit(3)
                .map(i -> String.format("[%.4f] %s", scores.get(i), candidates.get(i).text()))
                .toList();

        System.out.println("=== 有重排序结果（交叉编码器精排，minScore=0.1）===");
        rerankResults.forEach(r -> System.out.println("  · " + r));
        
        
//        ContentAggregator contentAggregator = ReRankingContentAggregator.builder()
//                .scoringModel ( scoringModel )
//                .minScore(0.8) 
//                .build();//精排后只要大于0.8数据
//        RetrievalAugmentor retrievalAugmentor = DefaultRetrievalAugmentor.builder()
//        	      .contentRetriever(contentRetriever)
//        	      .contentAggregator(contentAggregator)
//        	      .build();//RAG 检索增强器：将粗排 + 精排串联为完整流水线
//        chart(retrievalAugmentor);
    }
    
    public void chart(RetrievalAugmentor retrievalAugmentor) {
    	 ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);//最大保留信息
         
    	 OpenAiChatModel deepSeekModel =  OpenAiChatModel.builder()
                 .apiKey(apiKey)
                 .baseUrl("https://api.deepseek.com/v1") // DeepSeek官方API端点
                 .modelName("deepseek-chat") // 可选deepseek-reasoner（推理模型）
                 .temperature(1.3) // DeepSeek推荐>1.0以获得更好生成效果
                 .timeout(Duration.ofSeconds(60))
                 .maxTokens(1000)
                 .build();//封装对话大模型对象
         
         Assistant assistant=AiServices.builder(Assistant.class)
         .chatModel(deepSeekModel)
         .retrievalAugmentor(retrievalAugmentor)
         .chatMemory(chatMemory)
         .build();
         
         shared.Utils.startConversationWith(assistant);
    }
}