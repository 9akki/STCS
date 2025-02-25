package com.itheima.service;

import com.itheima.pojo.Classpojo;
import com.itheima.pojo.Student;

import java.util.List;

public interface ClassService {
    void addClass(Classpojo classpojo, Integer[] idList);

    void deleteClass(Integer id);

    Classpojo single(Integer id);

    List<Classpojo> listByTeacherId(Integer id);

    void updateClass(Classpojo classpojo);

    List<Student> Empty(Integer teacherID);
}
