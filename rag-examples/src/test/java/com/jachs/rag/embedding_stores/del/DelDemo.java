package com.jachs.rag.embedding_stores.del;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.mysql.cj.x.protobuf.MysqlxCrud.Collection;

import dev.langchain4j.data.document.loader.ClassPathDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.bgesmallenv15q.BgeSmallEnV15QuantizedEmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.IngestionResult;
import dev.langchain4j.store.embedding.filter.Filter;
import dev.langchain4j.store.embedding.filter.comparison.IsEqualTo;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;

/***
 * @author zhanchaohan
 */
public class DelDemo {
	EmbeddingModel model = new BgeSmallEnV15QuantizedEmbeddingModel();
	
	
//	@BeforeEach
	public void addInit() {
		 	EmbeddingModel model = new BgeSmallEnV15QuantizedEmbeddingModel();//自带模型
	        PgVectorEmbeddingStore embeddingStore =shared.Utils.initPvDb(true);
	        
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
	
	
	@Test
	public void t2() {
		PgVectorEmbeddingStore embeddingStore =shared.Utils.initPvDb(false);
		
//		embeddingStore.remove("eb20cbaa-91ef-4176-ab12-130679ff85d0");//Id删除
		
//		embeddingStore.removeAll(Arrays.asList("22c8aca3-571d-4b1a-a9d6-c8aee7987eef",
//				"e88cf0fc-7918-4cd5-ad22-6b8cccb3ad69"));//多ID删除
		
		Filter userIdFilter = new IsEqualTo("file_name","d.txt");//filter过滤器删除
		
		embeddingStore.removeAll(userIdFilter);
	}
	
	
	@Test
	public void t1() {
		String QUERY="一个";
		PgVectorEmbeddingStore embeddingStore =shared.Utils.initPvDb(false);
	    
	    Embedding queryEmbedding = model.embed(QUERY).content();
	    EmbeddingSearchRequest request = EmbeddingSearchRequest.builder()
	    		.queryEmbedding(queryEmbedding).build();
	    
	    
	    List<EmbeddingMatch<TextSegment>> results = embeddingStore.search(request).matches();
	    
	    for (EmbeddingMatch<TextSegment> embeddingMatch : results) {
			System.out.println(embeddingMatch.toString());
			System.out.println("\n");
		}
//	    embeddingStore.remove("");//Id删除
	    
	}
}
