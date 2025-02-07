package com.jachs.deepseek.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/***
 * @author zhanchaohan
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class messages {
    private String role;
    private String content;
}
