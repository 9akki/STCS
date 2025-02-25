package com.itheima.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Chatroom {
    private Integer id;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
