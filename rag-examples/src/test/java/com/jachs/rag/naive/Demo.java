package com.jachs.rag.naive;

import java.util.Arrays;
import java.util.List;

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
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.bgesmallenv15q.BgeSmallEnV15QuantizedEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
import shared.Assistant;

/***
 * @author zhanchaohan
 */
public class Demo {
	OpenAiChatModel chatModel =shared.Utils.chatLanguageModel();
    
    @Test
    public void t1() {
        List<Document> documents =Arrays.asList (  
                ClassPathDocumentLoader.
                loadDocument("documents/aa.txt", new TextDocumentParser()),
                ClassPathDocumentLoader.
                loadDocument("documents/bb.txt", new TextDocumentParser()));
        
        EmbeddingModel embeddingModel = new BgeSmallEnV15QuantizedEmbeddingModel();
        PgVectorEmbeddingStore embeddingStore =shared.Utils.initPvDb(true);
        
        DocumentSplitter splitter = DocumentSplitters.recursive(300, 0);
        
        for ( Document doc : documents ) {
            List<TextSegment> segments = splitter.split(doc);//切片
            List<Embedding> embeddings = embeddingModel.embedAll(segments).content();//向量化
            embeddingStore.addAll(embeddings, segments);//入库
        }
        
        
        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(20)
                .minScore(0.5)
                .build();
        
        ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);//最大保留信息
        
        
        Assistant assistant=AiServices.builder(Assistant.class)
        .chatModel(chatModel)
        .contentRetriever(contentRetriever)
        .chatMemory(chatMemory)
        .build();//切片向量数据传递给大模型，建立对话
        
        shared.Utils.startConversationWith(assistant);
    }
}
