package com.itheima.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuizItemScore {
    private Integer id;
    private Integer quizID;
    private Integer score;
    private Integer studentID;
    private Integer quizItemID;
}
