package com.itheima.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReflectionProcedure extends Procedure{
    private Integer id;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private LocalDateTime endTime;
    private Integer quizID;
}
