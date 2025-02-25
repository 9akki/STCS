package com.itheima.service;

import com.itheima.pojo.Group;
import com.itheima.pojo.Student;

import java.util.List;

public interface GroupService {
    void createGroup(Integer[] idList, Group group);

    List<Group> listByClassId(Integer id);

    Group single(Integer id);

    void delete(Integer id);

    List<Student> empty(Integer id, Integer classId);

    void update(Integer[] idList, Group group);

}
