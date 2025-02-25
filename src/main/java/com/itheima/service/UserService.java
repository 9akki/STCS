package com.itheima.service;


import com.itheima.pojo.Student;
import com.itheima.pojo.Teacher;
import com.itheima.pojo.UsersResult;

import java.util.List;

public interface UserService {
    Object studentLogin(String username, String password);

    Object teacherLogin(String username, String password);

    Student getStudentById(Integer id);

    UsersResult page(String name, Short gender, Integer page, Integer pageSize, String classname, String groupname, Integer teacherID,Integer classID);

    void deleteById(Integer id);

    void update(Student student);

    Boolean add(Student student);

    List<Student> getStudentByclassId(Integer id);

    List<Student> getStudentBygroupdId(Integer id);

    String canHeVote(Integer procedureID, Integer userID);

    String canTheyGoNext(Integer taskID, Integer userID);

    String createEtherpadSession(String authorID, String etherpadGroupID);

    Teacher getTeacherById(Integer id);

    Object adminLogin(String username, String password);

    Boolean addTeacher(Teacher teacher);

    void updateTeacher(Teacher teacher);

    void deleteTeacher(Integer id);

    List<Teacher> listTeacher();
}
