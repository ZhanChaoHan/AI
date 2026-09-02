package com.jachs.rag.advanced.sql;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import dev.langchain4j.experimental.rag.content.retriever.sql.SqlDatabaseContentRetriever;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
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
	OpenAiChatModel chatModel =shared.Utils.chatLanguageModel();
    
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
