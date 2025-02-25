package com.itheima.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CollaborativeProcedure extends Procedure{
    private Integer id;
    private LocalDateTime endTime;
    private LocalDateTime updateTime;
    private LocalDateTime createTime;
    private String padID;
    private String readonlyID;
}
