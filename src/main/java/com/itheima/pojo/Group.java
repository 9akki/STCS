package com.itheima.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Group {
    private Integer id;
    private String name;
    private Integer classId;
    private Integer memberNum;
    private LocalDateTime createTime;
    private  LocalDateTime updateTime;
    private String groupID;
}
