package com.jachs.rag.embedding_stores.query.filter;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.jachs.rag.doc.document_transformer.inter.SuffixTransformer;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentTransformer;
import dev.langchain4j.data.document.loader.ClassPathDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.parser.apache.tika.ApacheTikaDocumentParser;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.bgesmallenv15q.BgeSmallEnV15QuantizedEmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.filter.Filter;
import dev.langchain4j.store.embedding.filter.comparison.IsIn;
import dev.langchain4j.store.embedding.filter.comparison.IsNotIn;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;

/***
 * @author zhanchaohan
 */
public class De1 {
	EmbeddingModel model = new BgeSmallEnV15QuantizedEmbeddingModel();
	PgVectorEmbeddingStore embeddingStore=PgVectorEmbeddingStore.builder()
	        .host("localhost")
	        .port(5432)
	        .database("langchat")
	        .user("postgres")
	        .password("12345678")
	        .table("items")
	        .dropTableFirst(false)
	        .dimension(model.dimension()) 
	        .build();
	
	@Test
	public void initBase() {
		EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .embeddingModel(model)
                .embeddingStore(embeddingStore)
                .build();
		
		Document d1=ClassPathDocumentLoader.
                loadDocument("documents/a.txt", new TextDocumentParser());
		Document d2=ClassPathDocumentLoader.
                loadDocument("documents/t1.docx", new ApacheTikaDocumentParser());
        Document d3=ClassPathDocumentLoader.
                loadDocument("documents/t1.png", new ApacheTikaDocumentParser());
        
        DocumentTransformer documentTransformer=new SuffixTransformer();//文档转换后缀名
        
        documentTransformer.transformAll(Arrays.asList(d1,d2,d3));
        
        ingestor.ingest(Arrays.asList(d1,d2,d3));
	}
	
	
	@Test
	public void t1() {
		 	String QUERY="""
		 			一
		 			""";
	        Embedding queryEmbedding = model.embed(QUERY).content();
	        
//	        Filter filter=new ContainsString ( "file_name", "c.txt" );//包含子串
//	        Filter filter=new IsEqualTo ( "file_name", "a.txt" );//等于
	        Filter filter=new IsIn ( "suffix", Arrays.asList ( "JPG","PNG","jpg","png" ) );//IN 范围
	        
	        Filter filter1=new IsNotIn ( "suffix", Arrays.asList ( "JPG","PNG","jpg","png" ) );//Not IN 范围
	        
	        EmbeddingSearchResult<TextSegment> textSegmentList=embeddingStore.search(EmbeddingSearchRequest.builder()
	        		.minScore(0.1)
	        		.maxResults(5)
	        		.filter(filter1)
	        		.queryEmbedding(queryEmbedding)
	        		.build()
	        		);
	        
	        
	        for ( EmbeddingMatch<TextSegment> embeddingMatch : textSegmentList.matches() ) {
	            System.out.println ( "-----------**************-----------" );
	            System.out.println ( embeddingMatch.score());
	            System.out.println ( embeddingMatch.toString () );
	            System.out.println ( "-----------**************-----------" );
	            System.out.println ( "\n" );
	        }
	}
}
