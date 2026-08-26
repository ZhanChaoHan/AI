package com.jachs.langchain4j.embedding_stores.pg.query;


import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
import org.apache.poi.ss.formula.functions.LookupUtils;
import org.junit.Test;

import java.util.List;

/***
 * @author zhanchaohan
 */
public class Demo {

    @Test
    public void t1(){
        EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();

        PgVectorEmbeddingStore store = PgVectorEmbeddingStore.builder()
                .host("localhost")
                .port(5432)
                .database("langchat")
                .user("postgres")
                .password("12345678")
                .table("document_embeddings")
                .dimension(embeddingModel.dimension())  // 384 for AllMiniLmL6V2
                .build();


        EmbeddingModel model = new AllMiniLmL6V2EmbeddingModel();
        Embedding queryEmbedding = model.embed("问题文本").content();

        EmbeddingSearchRequest request = EmbeddingSearchRequest.builder()
                .queryEmbedding(queryEmbedding)
                .maxResults(1)//取最符合的几条数据，这里是1条
                .minScore(0.6)//只有符合0.6的才会选用，也可以不设置
                .build();

        List<EmbeddingMatch<TextSegment>> results = store.search(request).matches();

        for (EmbeddingMatch<TextSegment> result : results) {
            System.out.println(result.toString());


        }
    }
}
