package com.itheima.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Teacher extends User{
    private Integer id;
    private String username;
    private String name;
    private String password;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String email;
}
