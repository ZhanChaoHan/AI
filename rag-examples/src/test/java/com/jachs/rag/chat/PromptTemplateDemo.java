package com.jachs.rag.chat;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;

/***
 * @author zhanchaohan
 */
public class PromptTemplateDemo {
    
    
    //问题模板
    @Test
    public void t1() {
        Prompt prompt = PromptTemplate.from("How old is he as of {{current_date}}?").apply(Map.of());
        
        Map<String, Object> variables=new HashMap<String, Object> ();
        variables.put ( "name", "peter" );
        variables.put ( "age", 13);
        
        Prompt prompt1 = PromptTemplate
                .from("姓名: {{name}} 年龄:{{age}}")
                .apply(variables);
        
        System.out.println ( prompt.text () );
        System.out.println ( prompt1.text () );
    }
}
