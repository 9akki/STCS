package com.itheima.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuizItem {
    private Integer id;
    private Integer quizID;
    private String content;
    private Integer itemOrder;
}
