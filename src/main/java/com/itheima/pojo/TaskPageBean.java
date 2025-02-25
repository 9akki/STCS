package com.itheima.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskPageBean {
    private long total;//总记录数
    //这里不能将泛型改为Object，并且在使用构造方法的时候传入Student类型的参数吗？
    private List<Task> rows;//数据列表
}
