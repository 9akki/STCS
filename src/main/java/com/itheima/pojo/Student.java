package com.itheima.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student extends User{
    private Integer id;
    private String username;
    private String name;
    private String password;
    private Integer classId;
    private Integer groupId;
    private Integer gender;
    private String avatar;
    private LocalDateTime updateTime;
    private LocalDateTime createTime;
    private String email;
    private Integer teacherID;
}
