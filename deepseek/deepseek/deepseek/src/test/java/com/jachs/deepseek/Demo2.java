package com.jachs.deepseek;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.jachs.deepseek.entity.Body;
import com.jachs.deepseek.entity.messages;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/***
 * @author 79951
 */
public class Demo2 {
    //deepseek接口地址
    public static String URL="https://api.deepseek.com/chat/completions";
    OkHttpClient client = new OkHttpClient();
    Gson gson=new Gson();
    public static final MediaType JSON= MediaType.get("application/json; charset=utf-8");
    
    @Test
    public void t1() throws Exception {
        Body json_body=new Body();
        
        json_body.setModel ( "deepseek-chat" );
        json_body.setStream ( false );
        
        List<messages>messList=new ArrayList<messages> ();
        messList.add ( new messages ( "system", "1+1等于几?" ) );
        messList.add ( new messages ( "user", "画一条龙" ) );
        
        json_body.setMessages ( messList );
        RequestBody body = RequestBody.create(gson.toJson ( json_body ) , JSON);
        
        Request request = new Request.Builder()
                .addHeader ( "Authorization", "Bearer sk-1a29221c6ac04481a4b9d7f07318f591" )
                .url(URL)
                .post(body)
                .build();
        
        Response response = client.newCall(request).execute();
        
        System.out.println ( response.body().string());
    }
}
