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
import dev.langchain4j.store.embedding.filter.comparison.IsEqualTo;
import dev.langchain4j.store.embedding.filter.comparison.IsIn;
import dev.langchain4j.store.embedding.filter.comparison.IsNotIn;
import dev.langchain4j.store.embedding.filter.logical.And;
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
        Document d4=ClassPathDocumentLoader.
                loadDocument("documents/b.txt", new ApacheTikaDocumentParser());
        Document d5=ClassPathDocumentLoader.
                loadDocument("documents/c.txt", new ApacheTikaDocumentParser());
        
//        DocumentTransformer documentTransformer=new SuffixTransformer();//文档转换后缀名
//        documentTransformer.transformAll(Arrays.asList(d1,d2,d3));
        
        ingestor.ingest(Arrays.asList(d1,d2,d3,d4,d5));
	}
	
	
	@Test
	public void t1() {
		 	String QUERY="""
		 			一
		 			""";
	        Embedding queryEmbedding = model.embed(QUERY).content();
	        
//	        Filter filter=new ContainsString ( "file_name", "c.txt" );//包含子串
//	        Filter filter=new IsEqualTo ( "file_name", "a.txt" );//等于
//	        Filter filter=new IsIn ( "suffix", Arrays.asList ( "JPG","PNG","jpg","png" ) );//IN 范围
//	        Filter filter1=new IsNotIn ( "suffix", Arrays.asList ( "JPG","PNG","jpg","png" ) );//Not IN 范围
	        
	        Filter filter=new IsEqualTo("index", "0");
	        Filter filter1=new IsEqualTo("file_name", "a.txt");
	        
	        Filter combinedFilter = new And(filter, filter1);
	        
	        EmbeddingSearchResult<TextSegment> textSegmentList=embeddingStore.search(EmbeddingSearchRequest.builder()
	        		.minScore(0.1)
	        		.maxResults(5)
	        		.filter(combinedFilter)
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
	
	@Test
	public void flushData() {
		// 1. 构造查询用的嵌入向量（可使用任意合法Embedding，也可构造一个全0向量）
		Embedding queryEmbedding = model.embed("任意文本").content();

		// 2. 构建全表查询的EmbeddingSearchRequest
		EmbeddingSearchRequest request = EmbeddingSearchRequest.builder()
		        .queryEmbedding(queryEmbedding)
		        .maxResults(Integer.MAX_VALUE) // 设为最大整数，确保能返回所有向量
		        .minScore(0.0) // 相似度阈值设为0，不过滤任何结果
		        .filter(null) // 不添加任何元数据过滤条件
		        .build();
		// 3. 执行全表查询
		EmbeddingSearchResult<TextSegment> result = embeddingStore.search(request);

		// 4. 遍历获取全表所有向量数据
		List<EmbeddingMatch<TextSegment>> allMatches = result.matches();
		
		for ( EmbeddingMatch<TextSegment> embeddingMatch : allMatches ) {
            System.out.println ( "-----------**************-----------" );
            System.out.println ( embeddingMatch.score());
            System.out.println ( embeddingMatch.toString () );
            System.out.println ( "-----------**************-----------" );
            System.out.println ( "\n" );
        }
	}
	
	@Test
	public void hjc() {
		List<String> img=Arrays.asList("png","jpg");
		System.out.println(img.contains("png"));
	}
}
