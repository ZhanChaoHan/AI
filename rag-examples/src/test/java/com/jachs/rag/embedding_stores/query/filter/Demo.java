package com.jachs.rag.embedding_stores.query.filter;

import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.document.loader.ClassPathDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.bgesmallenv15q.BgeSmallEnV15QuantizedEmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.filter.MetadataFilterBuilder;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
import shared.entity.User;

/***
 * Metadata不支持直接存对象
 * 对象转Json之后也不能查询
 * @author zhanchaohan
 */
public class Demo {
	PgVectorEmbeddingStore store=shared.Utils.initPvDb(true);
	EmbeddingModel model = new BgeSmallEnV15QuantizedEmbeddingModel();//自带模型
	Gson gson=new GsonBuilder().disableHtmlEscaping().create();
	

	@Test
	public void t1() {
		EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .embeddingModel(model)
                .embeddingStore(store)
                .build();
		
		//写入第一个文档
		Document document=ClassPathDocumentLoader.
        loadDocument("documents/b.txt", new TextDocumentParser());
		
		User user=new User(123L,"Jack",'男');
		Metadata metadata=document.metadata();//Metadata 改写
		metadata.put("user", gson.toJson(user));

		ingestor.ingest(document);
		
		//写入第二个文档
		Document document1=ClassPathDocumentLoader.
		        loadDocument("documents/a.txt", new TextDocumentParser());
				
				User user1=new User(456L,"pter",'女');
				Metadata metadata1=document1.metadata();//Metadata 改写
				metadata1.put("user", gson.toJson(user1));

				ingestor.ingest(document1);
		
	}
	
	@Test
	public void filter() {
		String QUERY="""
	 			一
	 			""";
		 Embedding queryEmbedding = model.embed(QUERY).content();
		 EmbeddingSearchResult<TextSegment> textSegmentList=store.search(EmbeddingSearchRequest.builder()
				 .queryEmbedding(queryEmbedding)
				 .filter(MetadataFilterBuilder.metadataKey("user.userId").isEqualTo(123L))
				 .build());
		 
		 for ( EmbeddingMatch<TextSegment> embeddingMatch : textSegmentList.matches() ) {
	            System.out.println ( "-----------**************-----------" );
	            System.out.println ( embeddingMatch.score());
	            System.out.println ( embeddingMatch.toString () );
	            System.out.println ( "-----------**************-----------" );
	            System.out.println ( "\n" );
	        }
	}

}
