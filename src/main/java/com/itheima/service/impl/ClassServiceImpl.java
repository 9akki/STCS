package com.itheima.service.impl;

import com.itheima.mapper.ClassMapper;
import com.itheima.mapper.UserMapper;
import com.itheima.pojo.Classpojo;
import com.itheima.pojo.Student;
import com.itheima.service.ClassService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class ClassServiceImpl implements ClassService {

    @Autowired
    private ClassMapper classMapper;
    @Autowired
    private UserMapper userMapper;
    @Override
    public void addClass(Classpojo classpojo, Integer[] idList) {
        classpojo.setCreateTime(LocalDateTime.now());
        classpojo.setUpdateTime(LocalDateTime.now());
        if (idList!=null)
        classpojo.setStudentNum(idList.length);
        else classpojo.setStudentNum(0);
        classMapper.InsertClass(classpojo);
        Student student = new Student();
        student.setClassId(classpojo.getId());
        if( idList!=null&& idList.length!=0)
        for (Integer id : idList) {
            student.setId(id);
            student.setUpdateTime(LocalDateTime.now());
            userMapper.update(student);
        }
    }

    @Override
    public void deleteClass(Integer id) {
        List<Student> studentList = userMapper.getStudentByclassId(id);
        for (Student student : studentList) {
            userMapper.classIdToNull(student.getId());
        }
        classMapper.deleteClass(id);
    }

    @Override
    public Classpojo single(Integer id) {
        Classpojo classpojo = classMapper.getById(id);
        return classpojo;
    }

    @Override
    public List<Classpojo> listByTeacherId(Integer id) {
        List<Classpojo> classpojoList=classMapper.listByTeacherId(id);
        return classpojoList;
    }

    @Override
    public void updateClass(Classpojo classpojo) {
        classpojo.setUpdateTime(LocalDateTime.now());
        classMapper.updateClass(classpojo);
    }

    @Override
    public List<Student> Empty(Integer teacherID) {
        List<Student> studentList = classMapper.Empty(teacherID);
        return studentList;
    }
}
