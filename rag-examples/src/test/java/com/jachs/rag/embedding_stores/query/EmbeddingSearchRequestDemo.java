package com.jachs.rag.embedding_stores.query;

import java.util.Arrays;
import java.util.List;

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
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;


/***
 * @author zhanchaohan
EmbeddingSearchRequest 表示在 EmbeddingStore 中执行搜索的请求。 它具有以下属性：
<br>Embedding queryEmbedding：用作参考的嵌入向量。
<br>int maxResults：返回结果的最大数量。这是一个可选参数。默认值：3。
<br>double minScore：最低分数，范围从 0 到 1（含）。只有分数 >= minScore 的嵌入向量才会被返回。这是一个可选参数。默认值：0。
<br>Filter filter：搜索时应用于 Metadata 的过滤器。只有 Metadata 匹配 Filter 的 TextSegment 才会被返回。
 */
public class EmbeddingSearchRequestDemo {

    @Test
    public void t1() {
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
        
        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();//内存库
        EmbeddingStoreIngestor.ingest(documents, embeddingStore);//文档写入内存库
        

        //默认模型
        /**
langchain4j-embeddings-bge-small-en-v15-q 是 LangChain4j 框架提供的一个‌本地化、量化版‌的英文文本嵌入（Embedding）模型依赖包。它基于智源人工智能研究院（BAAI）发布的 bge-small-en-v1.5 模型，经过量化处理以减小体积并提升在 Java 应用中的运行效率。
以下是该依赖包的核心特性、使用场景及配置方法：
1. 核心特性
‌本地运行（In-Process）‌：模型直接运行在 Java 虚拟机进程中，基于 ONNX Runtime，无需调用外部 API，无网络延迟，数据完全私有。
‌量化优化（Quantized）‌：后缀 -q 表示该模型经过了量化处理（通常为 INT8 量化）。相比非量化版本，其模型文件更小（约 20-30MB），内存占用更低，推理速度更快，且精度损失极小。
‌英文专用‌：主要针对英文文本优化，在英文语义检索任务中表现优异。若用于中文场景，效果可能不如专门的中文模型（如 bge-small-zh）。
‌向量维度‌：生成的向量维度为 ‌384‌。
‌输入限制‌：虽然技术上支持较长文本，但建议单个文本片段不超过 ‌512 tokens‌，以保证嵌入质量。对于查询（Query），建议添加前缀 "Represent this sentence for searching relevant passages:" 以提升检索匹配度。
         */
        EmbeddingModel model = new BgeSmallEnV15QuantizedEmbeddingModel();
        
        String QUERY="一个苹果";
        Embedding queryEmbedding = model.embed(QUERY).content();
        
        EmbeddingSearchRequest rmbeddingSearchRequest=EmbeddingSearchRequest.builder ()
                .queryEmbedding ( queryEmbedding )
                .maxResults ( 4 )
                .minScore ( 0.6 )
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
