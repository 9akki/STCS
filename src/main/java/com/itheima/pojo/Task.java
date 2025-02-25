package com.itheima.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    private Integer id;
    private String topic;
    private Integer classID;
    private LocalDateTime endTime;
    private LocalDateTime updateTime;
    private LocalDateTime createTime;
    private String procedureOrderList;
    private LocalDateTime startTime;
}
