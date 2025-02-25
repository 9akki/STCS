package com.itheima.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Announcement {
    private Integer id;
    private Integer taskID;
    private String content;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
