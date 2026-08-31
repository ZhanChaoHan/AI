package com.jachs.rag.embedding_stores.add;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.junit.jupiter.api.Test;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentByCharacterSplitter;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.bgesmallenv15q.BgeSmallEnV15QuantizedEmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;

/***
 * @author zhanchaohan
 */
public class T1 {

	@Test
	public void t1() throws FileNotFoundException {
	    EmbeddingModel embeddingModel = new BgeSmallEnV15QuantizedEmbeddingModel();
		DocumentByCharacterSplitter splitter = new DocumentByCharacterSplitter(
				20,         // 每段最长字数
				10          // 自然语言最大重叠字数
		);

		TextDocumentParser parser = new TextDocumentParser();
		InputStream inputStream = new FileInputStream("D:\\code\\git\\AI\\langchain4j\\src\\test\\java\\com\\jachs\\langchain4j\\embedding_stores\\pg\\add\\a.txt");
		Document document = parser.parse(inputStream);
		String text = document.text();

		EmbeddingStore<TextSegment> embeddingStore = PgVectorEmbeddingStore.builder()
				.host("localhost")
				.port(5432)
				.database("langchat")
				.user("postgres")
				.password("12345678")
				.table("document_embeddings")
				.dropTableFirst ( true )
				.dimension(embeddingModel.dimension())  // 384 for AllMiniLmL6V2
				.build();

		EmbeddingStoreIngestor.builder()
				.documentSplitter(splitter)
				.embeddingModel(embeddingModel)
				.embeddingStore(embeddingStore)
				.build()
				.ingest(document);
	}
}
