package shared;

import static dev.langchain4j.internal.Utils.getOrDefault;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.bgesmallenv15q.BgeSmallEnV15QuantizedEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;



/***
 * @author zhanchaohan
 */
public class Utils {
	InMemoryEmbeddingStore<TextSegment>  inMemoryEmbeddingStore=new InMemoryEmbeddingStore<TextSegment>();//本地内存库
    public static final String OPENAI_API_KEY = getOrDefault(System.getenv("OPENAI_API_KEY"), "demo");

    public static PgVectorEmbeddingStore initPvDb(boolean del) {
    	EmbeddingModel model = new BgeSmallEnV15QuantizedEmbeddingModel();
    	
    	 return PgVectorEmbeddingStore.builder()
                 .host("localhost")
                 .port(5432)
                 .database("langchat")
                 .user("postgres")
                 .password("12345678")
                 .table("document_embeddings")
                 .dropTableFirst(del)
                 .dimension(model.dimension())  // 384 for AllMiniLmL6V2
                 .build();
    }
    
    public static PgVectorEmbeddingStore initPvDb2(boolean del,int dimension,String table) {
    	EmbeddingModel model = new BgeSmallEnV15QuantizedEmbeddingModel();
    	
    	 return PgVectorEmbeddingStore.builder()
                 .host("localhost")
                 .port(5432)
                 .database("langchat")
                 .user("postgres")
                 .password("12345678")
                 .table(table)
                 .dropTableFirst(del)
                 .dimension(dimension)
                 .build();
    }
    
    public static OpenAiChatModel chatLanguageModel() {
    	String apiKey = System.getenv("deepseek-key");
    	return  OpenAiChatModel.builder()
                .apiKey(apiKey)
                .baseUrl("https://api.deepseek.com/v1") // DeepSeek官方API端点
                .modelName("deepseek-chat") // 可选deepseek-reasoner（推理模型）
                .temperature(1.3) // DeepSeek推荐>1.0以获得更好生成效果
                .timeout(Duration.ofSeconds(60))
                .maxTokens(1000)
                .build();//封装对话大模型对象
    }
    
    
    public static void startConversationWith(Assistant assistant) {
        Logger log = LoggerFactory.getLogger(Assistant.class);
        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                log.info("==================================================");
                log.info("User: ");
                String userQuery = scanner.nextLine();
                log.info("==================================================");

                if ("exit".equalsIgnoreCase(userQuery)) {
                    break;
                }

                String agentAnswer = assistant.answer(userQuery);
                log.info("==================================================");
                log.info("Assistant: " + agentAnswer);
            }
        }
    }

    public static Assistant assistant(ContentRetriever contentRetriever) {
    	return AiServices.builder(Assistant.class).chatMemory ( MessageWindowChatMemory.withMaxMessages(10) )
    	        .contentRetriever ( contentRetriever )
    	        .chatLanguageModel(chatLanguageModel())
    	        .build ();
    	        
    }
    
    public static void chartWithAssistant(ContentRetriever contentRetriever) {
    	Assistant assistant= AiServices.builder(Assistant.class).chatMemory ( MessageWindowChatMemory.withMaxMessages(10) )
    	        .contentRetriever ( contentRetriever )
    	        .chatLanguageModel(chatLanguageModel())
    	        .build ();
    	        
    	shared.Utils.startConversationWith(assistant);
    }
    
    
    public static PathMatcher glob(String glob) {
        return FileSystems.getDefault().getPathMatcher("glob:" + glob);
    }

    public static Path toPath(String relativePath) {
        try {
            URL fileUrl = Utils.class.getClassLoader().getResource(relativePath);
            return Paths.get(fileUrl.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
