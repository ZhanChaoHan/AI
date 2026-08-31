package com.jachs.rag.embedding_stores;

import org.junit.jupiter.api.Test;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;

/***
 * datasource	用于数据库连接的 DataSource 对象。如果未提供，则必须单独提供 host、port、user、password 和 database。	无	如果未单独提供 host、port、user、password 和 database，则必需。
 * host	PostgreSQL 服务器的主机名。如果未提供 DataSource，则必需。	无	如果未提供 DataSource，则必需
 * port	PostgreSQL 服务器的端口号。如果未提供 DataSource，则必需。	无	如果未提供 DataSource，则必需
 * user	数据库认证的用户名。如果未提供 DataSource，则必需。	无	如果未提供 DataSource，则必需
 * password	数据库认证的密码。如果未提供 DataSource，则必需。	无	如果未提供 DataSource，则必需
 * database	要连接的数据库名称。如果未提供 DataSource，则必需。	无	如果未提供 DataSource，则必需
 * table	用于存储嵌入的数据库表名称。	无	必需
 * dimension	嵌入向量的维度。这应该与所使用的嵌入模型匹配。使用 embeddingModel.dimension() 动态设置它。	无	必需
 * useIndex	IVFFlat 索引将向量分为多个列表，然后搜索最接近查询向量的那些列表的子集。它具有更快的构建时间并使用比 HNSW 更少的内存，但查询性能较低（在速度-召回权衡方面）。应使用 IVFFlat 索引。	false	可选
 * indexListSize	IVFFlat 索引的列表数量。	无	何时必需：如果 useIndex 为 true，则必须提供 indexListSize 并且必须大于零。否则，程序将在表初始化期间抛出异常。何时可选：如果 useIndex 为 false，则忽略此属性，不需要设置。
 * createTable	指定是否自动创建嵌入表。	true	可选
 * dropTableFirst	指定是否在重新创建表之前删除表（对测试有用）。	false	可选
 * metadataStorageConfig	用于处理与嵌入相关的元数据的配置对象。支持三种存储模式：
 * COLUMN_PER_KEY：适用于静态元数据，当您预先知道元数据键时。
 * COMBINED_JSON：适用于动态元数据，当您不预先知道元数据键时。将数据存储为 JSON。（默认）
 * COMBINED_JSONB：类似于 JSON，但以二进制格式存储，用于优化大型数据集的查询。	COMBINED_JSON	可选。如果未设置，则使用默认配置 COMBINED_JSON。
 * @author zhanchaohan
 */
public class PGVector {

    @Test
    public void t1() {
        EmbeddingStore<TextSegment> embeddingStore = PgVectorEmbeddingStore.builder()
                .host("localhost")                           // Required: Host of the PostgreSQL instance
                .port(5432)                                  // Required: Port of the PostgreSQL instance
                .database("postgres")                        // Required: Database name
                .user("my_user")                             // Required: Database user
                .password("my_password")                     // Required: Database password
                .table("my_embeddings")                      // Required: Table name to store embeddings
                .dimension(1)       // Required: Dimension of embeddings
                .build();
    }
    
    
    
    
//    @Test
//    public void t2() {
//        EmbeddingStore<TextSegment> embeddingStore = PgVectorEmbeddingStore.builder()
//                // Required parameters
//                .host("localhost")
//                .port(5432)
//                .database("postgres")
//                .user("my_user")
//                .password("my_password")
//                .table("my_embeddings")
//                .dimension(1)
//
//                // Optional parameters
//                .useIndex(true)                             // Enable IVFFlat index
//                .indexListSize(100)                         // Number of lists for IVFFlat index
//                .createTable(true)                          // Automatically create the table if it doesn’t exist
//                .dropTableFirst(false)                      // Don’t drop the table first (set to true if you want a fresh start)
//                .metadataStorageConfig(MetadataStorageConfig.combinedJsonb()) // Store metadata as a combined JSONB column
//
//                .build();
//    }
    
}
