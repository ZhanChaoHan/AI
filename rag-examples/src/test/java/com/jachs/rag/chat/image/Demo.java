package com.jachs.rag.chat.image;

import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.Test;

import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.data.message.*;


/***
 * @author zhanchaohan
 */
public class Demo {

	@Test
	public void t1() throws Exception {
		OpenAiChatModel chatModel=shared.Utils.chatLanguageModel();
		
		Path imagePath = Path.of("D:\\image\\A.jpg");
        byte[] imageBytes = Files.readAllBytes(imagePath);
        String base64 = Base64.encodeBase64String(imageBytes);

        UserMessage userMessage = UserMessage.from(
                TextContent.from("这张图片里有什么？请帮我解读一下。"),
                ImageContent.from(base64, "image/png")  // MIME 类型要和实际文件一致
        );
        
        String response = chatModel.chat(userMessage).aiMessage().text();
        System.out.println(response);
        
	}
}
