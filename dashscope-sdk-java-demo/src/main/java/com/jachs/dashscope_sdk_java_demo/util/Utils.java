package com.jachs.dashscope_sdk_java_demo.util;

import java.util.List;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;

/***
 * @author zhanchaohan
 */
public class Utils {
	
	public static PgVectorEmbeddingStore initPvDb2(boolean del, int dimension, String table) {
		return PgVectorEmbeddingStore.builder()
				.host("localhost").port(5432)
				.database("langchat").user("postgres")
				.password("12345678")
				.table(table).dropTableFirst(del)
				.dimension(dimension).build();
	}

	public static float[] DoubleToFloat(List<Double> vector) {
		float[] floatVector = new float[vector.size()];
		for (int i = 0; i < vector.size(); i++) {
			floatVector[i] = vector.get(i).floatValue();
		}
		return floatVector;
	}
	
	public static void printEmbeddingMatch(List<EmbeddingMatch<TextSegment>> matches) {
		for ( EmbeddingMatch<TextSegment> embeddingMatch : matches ) {
            System.out.println ( "-----------**************-----------" );
            System.out.println ( embeddingMatch.score () );
            System.out.println ( embeddingMatch.toString () );
            System.out.println ( "-----------**************-----------" );
            System.out.println ( "\n" );
        }
	}
}
