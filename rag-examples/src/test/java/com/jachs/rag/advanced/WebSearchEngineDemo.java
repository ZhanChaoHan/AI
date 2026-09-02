package com.jachs.rag.advanced;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import dev.langchain4j.service.AiServices;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.web.search.WebSearchTool;
import dev.langchain4j.web.search.searchapi.SearchApiWebSearchEngine;
import shared.Assistant;

/****
 * @author zhanchaohan
 * @see https://docs.langchain4j.info/category/web-search-engines
 */
public class WebSearchEngineDemo {
    private static final String SEARCHAPI_API_KEY = "YOUR_SEARCHAPI_KEY";
    private static final String OPENAI_API_KEY = "YOUR_OPENAI_KEY";
    
    OpenAiChatModel chatModel =shared.Utils.chatLanguageModel();
    
    
	@Test
	public void t1() {
		Map<String, Object> optionalParameters = new HashMap<>();
        optionalParameters.put("gl", "us");
        optionalParameters.put("hl", "en");
        optionalParameters.put("google_domain", "google.com");
        
        SearchApiWebSearchEngine searchEngine = SearchApiWebSearchEngine.builder()
                .apiKey(SEARCHAPI_API_KEY)
                .engine("google")
                .optionalParameters(optionalParameters)
                .build();
        
        

        WebSearchTool webTool = WebSearchTool.from(searchEngine);

//        Assistant assistant = AiServices.builder(Assistant.class)
//                .chatModel(chatModel)
//                .tools(webTool)
//                .build();
//
//        String answer = assistant.answer("我的家人下周要来马德里看我，列出适合全家人的最佳旅游活动");
//        System.out.println(answer);
        /*
            以下是马德里适合全家人的一些最佳旅游活动：
            
            1. **丽池公园 (Parque del Retiro)** - 一个美丽的公共公园，家庭可以在那里享受自然和各种活动。
            2. **普拉多博物馆 (Prado Museum)** - 一个著名的艺术博物馆，对成人和儿童都很有吸引力。
            3. **圣米格尔市场 (Mercado de San Miguel)** - 一个可以探索和品尝美味西班牙美食的市场。
            4. **皇宫 (Royal Palace)** - 探索马德里皇宫的宏伟。
            5. **马约尔广场 (Plaza Mayor)** 和 **太阳门广场 (Puerta del Sol)** - 具有活力氛围的历史广场。
            6. **伯纳乌球场 (Santiago Bernabeu Stadium)** - 非常适合体育爱好者和足球迷。
            7. **格兰大道 (Gran Via)** - 一条著名的购物、娱乐和观光街道。
            8. **国家考古博物馆 (National Archaeological Museum)** - 通过考古文物了解西班牙丰富的历史。
            9. **德沃德神庙 (Templo de Debod)** - 马德里市中心的一座古埃及神庙。
         */
    }
}
