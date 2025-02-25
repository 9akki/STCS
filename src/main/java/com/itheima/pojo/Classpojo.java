package com.itheima.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Classpojo {
    private Integer id;
    private String name;
    private Integer teacherId;
    private Integer studentNum;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
