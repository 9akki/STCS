package com.itheima.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

//分页查询的返回结果封装类
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentPageBean {
    private long total;//总记录数
    //这里不能将泛型改为Object，并且在使用构造方法的时候传入Student类型的参数吗？
    private List<Student> rows;//数据列表
}
