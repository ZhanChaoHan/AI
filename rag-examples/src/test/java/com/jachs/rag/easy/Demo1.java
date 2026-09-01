package com.jachs.rag.easy;


import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.ClassPathDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.bgesmallenv15q.BgeSmallEnV15QuantizedEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
import shared.Assistant;


/***
 * @author zhanchaohan
 */
public class Demo1 {
	OpenAiChatModel chatModel =shared.Utils.chatLanguageModel();
    EmbeddingModel embeddingModel = new BgeSmallEnV15QuantizedEmbeddingModel();
    
    
    @Test
    public void t1() {
        List<Document> documents =Arrays.asList (  
                ClassPathDocumentLoader.
                loadDocument("documents/a.txt", new TextDocumentParser()),
                ClassPathDocumentLoader.
                loadDocument("documents/b.txt", new TextDocumentParser()));
        
        PgVectorEmbeddingStore embeddingStore =shared.Utils.initPvDb(true);
       
        EmbeddingStoreIngestor.ingest(documents, embeddingStore);//文档写入内存库
        
        ContentRetriever contentRetriever=EmbeddingStoreContentRetriever.from(embeddingStore);
       
        
        Assistant assistant= AiServices.builder(Assistant.class).chatMemory ( MessageWindowChatMemory.withMaxMessages(10) )
        .contentRetriever ( contentRetriever )
        .chatModel(chatModel)
        .build ();
        
        shared.Utils.startConversationWith(assistant);
    }
}
