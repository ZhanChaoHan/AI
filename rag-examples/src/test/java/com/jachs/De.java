package com.jachs;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class De {
		public static void main(String[] args) {
		        
//		        // 预编译正则，提高性能
//		        Pattern pattern = Pattern.compile("(\\w+)=([^,]+)");
//		        
//		        Matcher matcher = pattern.matcher(input);
//		        Map<String, String> map = new HashMap<>();
//	        while (matcher.find()) {
//	            String key = matcher.group(1);
//	            String value = matcher.group(2);
//	            map.put(key, value);
//	            System.out.println("Key: " + key + ", Value: " + value);
//	        }
			Pattern name2 = Pattern.compile("^[\\u4e00-\\u9fa5a-zA-Z0-9()\\-_]+$");
			 Matcher m = name2.matcher("ad&^%$wd");
			 
			 System.out.println(m.find());
		}
}
