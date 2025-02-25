package com.itheima.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Admin extends User{
    private Integer id;
    private String username;
    private String name;
    Integer isDelete;
}
