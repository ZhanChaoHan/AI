package com.jachs.rag.embedding_stores.query;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dev.langchain4j.data.document.Document;
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
import dev.langchain4j.store.embedding.filter.comparison.IsIn;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;

/***
Filter 允许在执行向量搜索时按 Metadata 条目进行过滤。
目前支持以下 Filter 类型/操作：
IsEqualTo
IsNotEqualTo
IsGreaterThan
IsGreaterThanOrEqualTo
IsLessThan
IsLessThanOrEqualTo
IsIn
IsNotIn
ContainsString
And
Not
Or
 * @author zhanchaohan
 */
public class FilterDemo {

	@BeforeEach
    public void initDb() {
		 List<Document> documents =Arrays.asList (  
	                ClassPathDocumentLoader.
	                loadDocument("documents/a.txt", new TextDocumentParser()),
	                ClassPathDocumentLoader.
	                loadDocument("documents/b.txt", new TextDocumentParser()),
	                ClassPathDocumentLoader.
	                loadDocument("documents/c.txt", new TextDocumentParser()),
	                ClassPathDocumentLoader.
	                loadDocument("documents/d.txt", new TextDocumentParser()),
	                ClassPathDocumentLoader.
	                loadDocument("documents/e.txt", new TextDocumentParser())
	                );
	        
	        PgVectorEmbeddingStore embeddingStore =shared.Utils.initPvDb(true);
	        
	        IngestionResult ingestionResult= EmbeddingStoreIngestor.ingest(documents, embeddingStore);
	        
	        System.out.println(ingestionResult.tokenUsage().totalTokenCount());
	        
    }
	
	
    @Test
    public void t1() {
        PgVectorEmbeddingStore embeddingStore =shared.Utils.initPvDb(false);
        EmbeddingModel model = new BgeSmallEnV15QuantizedEmbeddingModel();
        
        String QUERY="一个";
        Embedding queryEmbedding = model.embed(QUERY).content();
        
//        Filter filter=new ContainsString ( "file_name", "c.txt" );//包含子串
//        Filter filter=new IsEqualTo ( "file_name", "a.txt" );//等于
        Filter filter=new IsIn ( "file_name", Arrays.asList ( "a.txt","b.txt" ) );//IN 范围
        
        EmbeddingSearchRequest rmbeddingSearchRequest=EmbeddingSearchRequest.builder ()
                .queryEmbedding ( queryEmbedding )
                .maxResults ( 5 )
                .minScore ( 0.6 )
                .filter ( filter )
                .build ();
        
        List<EmbeddingMatch<TextSegment>> results = embeddingStore.search(rmbeddingSearchRequest).matches();
        
        for ( EmbeddingMatch<TextSegment> embeddingMatch : results ) {
            System.out.println ( "-----------**************-----------" );
            System.out.println ( embeddingMatch.score () );
            System.out.println ( embeddingMatch.toString () );
            System.out.println ( "-----------**************-----------" );
            System.out.println ( "\n" );
        }
        
    }
}
