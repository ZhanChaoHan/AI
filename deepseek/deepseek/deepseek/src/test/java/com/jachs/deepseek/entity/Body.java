package com.jachs.deepseek.entity;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/***
 * @author zhanchaohan
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Body {
    private String model;
    private Boolean stream;
    
    private List<messages>messages;
}
