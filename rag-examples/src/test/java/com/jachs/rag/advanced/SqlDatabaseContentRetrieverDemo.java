package com.jachs.rag.advanced;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import dev.langchain4j.experimental.rag.content.retriever.sql.SqlDatabaseContentRetriever;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.service.AiServices;
import shared.Assistant;

/****
SQL 数据库内容检索器
SqlDatabaseContentRetriever 是 ContentRetriever 的一个实验性实现，
 可在 langchain4j-experimental-sql 模块中找到。
它使用 DataSource 和 LLM 来为给定的自然语言 Query 生成并执行 SQL 查询。
更多信息请参阅 SqlDatabaseContentRetriever 的 javadoc。
 * @author zhanchaohan
 */
public class SqlDatabaseContentRetrieverDemo {
	String apiKey = System.getenv("deepseek-key");
	
    ChatLanguageModel  chatModel=(ChatLanguageModel) OpenAiChatModel.builder()
    .apiKey(apiKey)
    .baseUrl("https://api.deepseek.com/v1") // DeepSeek官方API端点
    .modelName("deepseek-chat") // 可选deepseek-reasoner（推理模型）
    .temperature(1.3) // DeepSeek推荐>1.0以获得更好生成效果
    .timeout(Duration.ofSeconds(60))
    .maxTokens(1000)
    .build();//封装对话大模型对象
    
	  private static DataSource createDataSource() throws ClassNotFoundException {
		  	HikariConfig config = new HikariConfig();
	        config.setJdbcUrl("jdbc:mysql://localhost:3306/langchat?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
	        config.setUsername("root");
	        config.setPassword("123456");

	        String createTablesScript ="""
	        		CREATE TABLE `demo` (
					  `id` INT NOT NULL AUTO_INCREMENT,
					  `name` VARCHAR(255) DEFAULT NULL,
					  PRIMARY KEY (`id`)
					) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
	        		""";
	        
	        
	        HikariDataSource dataSource = new HikariDataSource(config);
	        execute(createTablesScript,dataSource );

	        execute("insert into demo(name)  values('张三');", dataSource);
	        execute("insert into demo(name)  values('李四');", dataSource);
	        execute("insert into demo(name)  values('wangwu ');", dataSource);

	        return dataSource;
	    }
	  
	  private static void execute(String sql, DataSource dataSource) {
	        try  {
	        	Connection connection = dataSource.getConnection();
        		Statement statement = connection.createStatement();
        		statement.execute(sql);
	        } catch (SQLException e) {
	            throw new RuntimeException(e);
	        }
	    }
	  
	  
	@Test
	public void t1() throws ClassNotFoundException {
		ContentRetriever contentRetriever = SqlDatabaseContentRetriever.builder()
                .dataSource(createDataSource())
                .chatLanguageModel(chatModel)
                .build();
		
		Assistant  assistant =AiServices.builder(Assistant.class)
        .chatLanguageModel(chatModel)
        .contentRetriever(contentRetriever)
        .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
        .build();
		
		shared.Utils.startConversationWith(assistant);
	}
}
