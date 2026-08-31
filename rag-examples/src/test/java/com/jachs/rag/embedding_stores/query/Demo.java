package com.jachs.rag.embedding_stores.query;


import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.bgesmallenv15q.BgeSmallEnV15QuantizedEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;

/***
 * @author zhanchaohan
 */
public class Demo {

    static final String QUERY="渡远荆门外，来从楚国游。";


    @Test
    public void t1(){
        EmbeddingModel model = new BgeSmallEnV15QuantizedEmbeddingModel();

        PgVectorEmbeddingStore store = PgVectorEmbeddingStore.builder()
                .host("localhost")
                .port(5432)
                .database("langchat")
                .user("postgres")
                .password("12345678")
                .table("document_embeddings")
                .dropTableFirst(true)
                .dimension(model.dimension())  // 384 for AllMiniLmL6V2
                .build();


        Embedding queryEmbedding = model.embed(QUERY).content();

        EmbeddingSearchRequest request = EmbeddingSearchRequest.builder()
                .queryEmbedding(queryEmbedding)
                .maxResults(1)//取最符合的几条数据，这里是1条
                .minScore(0.6)//只有符合0.6的才会选用，也可以不设置
                .build();

        List<EmbeddingMatch<TextSegment>> results = store.search(request).matches();

        for (EmbeddingMatch<TextSegment> result : results) {
            System.out.println(result.toString());
        }

        String question="输出这句古诗的全部信息";
        String context = results.stream()
                .map(match -> match.embedded().text())
                .collect(Collectors.joining("\n\n"));

        String promptWithContext = String.format("""
        基于Context:回答问题Question:
        Context:
        %s

        Question: %s

        Answer:
        """, context, question);

        ChatLanguageModel mode= OpenAiChatModel.builder()
                .baseUrl("https://api.deepseek.com/v1")
                .apiKey(System.getenv("homePC"))
                .modelName("deepseek-chat")
                .temperature(0.3)
                .maxTokens(2048)
                .timeout(Duration.ofSeconds(60))
                .build();

        String answer = mode.chat(promptWithContext);
        System.out.println("Answer: " + answer);
    }
    @Test
    public void dd(){
        System.out.println(System.getenv("homePC"));
    }
}
