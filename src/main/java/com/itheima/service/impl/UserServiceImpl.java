package com.itheima.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.config.EtherpadConfig;
import com.itheima.mapper.ClassMapper;
import com.itheima.mapper.GroupMapper;
import com.itheima.mapper.UserMapper;
import com.itheima.pojo.*;
import com.itheima.service.UserService;
import com.itheima.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private EtherpadConfig etherpadConfig;
    @Autowired
    private ClassMapper classMapper;
    @Autowired
    private GroupMapper groupMapper;

    @Override
    public Object studentLogin(String username, String password) {
        Student student = userMapper.getStudentByUsernameAndPassword(username, password);
        return student;
    }

    @Override
    public Object teacherLogin(String username, String password) {
        Teacher teacher = userMapper.getTeacherByUsernameAndPassword(username, password);
        return teacher;
    }

    @Override
    public Student getStudentById(Integer id) {
        Student student = userMapper.getStudentById(id);
        return student;
    }

    @Transactional
    @Override
    public UsersResult page(String name, Short gender, Integer page, Integer pageSize, String classname, String groupname, Integer teacherID, Integer classID) {
        PageHelper.startPage(page, pageSize);
        List<Student> studentList = userMapper.page(name, gender, classname, groupname, teacherID, classID);
        List<String> classNameList = new ArrayList<>();
        List<String> groupNameList = new ArrayList<>();
        for (Student student : studentList) {
            if (classMapper.getById(student.getClassId()) != null)
                classNameList.add(classMapper.getById(student.getClassId()).getName());
            else classNameList.add(null);
            if (groupMapper.single(student.getGroupId()) != null)
                groupNameList.add(groupMapper.single(student.getGroupId()).getName());
            else groupNameList.add(null);
        }
        log.info("select语句的返回结果：" + studentList);
        Page<Student> p = (Page<Student>) studentList;
        log.info("分页语句的返回结果：" + p);
        StudentPageBean studentPageBean = new StudentPageBean(p.getTotal(), p.getResult());
        UsersResult result = new UsersResult(studentPageBean, classNameList, groupNameList);
        return result;
    }

    @Transactional
    @Override
    public void deleteById(Integer id) {
        Student student = userMapper.getStudentById(id);
        classMapper.setClassNum(student.getClassId(), -1);
        groupMapper.setGroupNum(student.getGroupId(), -1);
        userMapper.deleteById(id);
    }

    @Transactional
    @Override
    public void update(Student student) {
        student.setUpdateTime(LocalDateTime.now());
        Student pastStudent = userMapper.getStudentById(student.getId());

        if (pastStudent.getClassId() != null)
            classMapper.setClassNum(pastStudent.getClassId(), -1);
        if (pastStudent.getGroupId() != null)
            groupMapper.setGroupNum(pastStudent.getGroupId(), -1);
        if (student.getClassId() != null)
            classMapper.setClassNum(student.getClassId(), 1);
        if (student.getGroupId() != null)
            groupMapper.setGroupNum(student.getGroupId(), 1);

        userMapper.update(student);
    }

    @Transactional
    @Override
    public Boolean add(Student student) {
        try {
            Integer studentCount = userMapper.getStudentByUsername(student.getUsername());
            if (studentCount>0){
                return false;
            }
            student.setUpdateTime(LocalDateTime.now());
            student.setPassword("123456");
            student.setCreateTime(LocalDateTime.now());
            userMapper.add(student);
            if (student.getClassId() != null) {
                if (classMapper.getById(student.getClassId()) != null)
                    classMapper.setClassNum(student.getClassId(), 1);
            }
            if (student.getGroupId() != null) {
                if (groupMapper.single(student.getGroupId()) != null)
                    groupMapper.setGroupNum(student.getGroupId(), 1);
            }
            HashMap<String, String> hm = new HashMap<>();
            hm.put("apikey", etherpadConfig.getApikey());
            hm.put("name", student.getName());
            hm.put("authorMapper", String.valueOf(student.getId()));

            String res = HttpUtils.get(etherpadConfig.getEtherpadUrl() + etherpadConfig.getCreateAuthorIfNotExistsFor(), hm);
            if (res == null) {
                throw new IllegalStateException("Http request returned null response");
            }
            JSONObject jsonObject = JSONObject.parseObject(res);
            JSONObject data = jsonObject.getJSONObject("data");
            String authorID = data.getString("authorID");
            student.setAuthorID(authorID);
            userMapper.update(student);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public List<Student> getStudentByclassId(Integer id) {
        List<Student> studentList = userMapper.getStudentByclassId(id);
        return studentList;
    }

    @Override
    public List<Student> getStudentBygroupdId(Integer id) {
        List<Student> studentList = userMapper.getStudentBygroupdId(id);
        return studentList;
    }

    @Override
    public String canHeVote(Integer procedureID, Integer userID) {
        if (userMapper.canHeVote(procedureID, userID) != 0)
            return "no";
        else return "yes";
    }

    @Override
    public String canTheyGoNext(Integer taskID, Integer userID) {
        if (userMapper.canTheyGoNext(taskID, userID) != 0)
            return "no";
        else return "yes";
    }

    @Transactional
    @Override
    public String createEtherpadSession(String authorID, String etherpadGroupID) {
        Long validUntil = System.currentTimeMillis() + 3600000 * 6;
        HashMap<String, String> hm = new HashMap();
        hm.put("apikey", etherpadConfig.getApikey());
        hm.put("groupID", etherpadGroupID);
        hm.put("authorID", authorID);
        hm.put("validUntil", String.valueOf(validUntil));
        String res = HttpUtils.get(etherpadConfig.getEtherpadUrl() + etherpadConfig.getCreateSession(), hm);
        if (res == null) {
            throw new IllegalStateException("Http request returned null response");
        }
        log.info("返回结果：" + res);
        JSONObject jsonObject = JSONObject.parseObject(res);
        JSONObject data = jsonObject.getJSONObject("data");
        String sessionID = data.getString("sessionID");
        return sessionID;
    }

    @Override
    public Teacher getTeacherById(Integer id) {
        Teacher teacher = userMapper.getTeacherById(id);
        return teacher;
    }

    @Override
    public Object adminLogin(String username, String password) {
        Admin admin = userMapper.getAdminByUsernameAndPassword(username, password);
        return admin;
    }

    @Transactional
    @Override
    public Boolean addTeacher(Teacher teacher) {
        try {
            Integer teacherCount = userMapper.getTeacherByUsername(teacher.getUsername());
            if (teacherCount>0){
                return false;
            }
            teacher.setUpdateTime(LocalDateTime.now());
            teacher.setPassword("123456");
            teacher.setCreateTime(LocalDateTime.now());
            userMapper.addTeacher(teacher);

            HashMap<String, String> hm = new HashMap<>();
            hm.put("apikey", etherpadConfig.getApikey());
            hm.put("name", teacher.getName());
            hm.put("authorMapper", String.valueOf(teacher.getId()));

            String res = HttpUtils.get(etherpadConfig.getEtherpadUrl() + etherpadConfig.getCreateAuthorIfNotExistsFor(), hm);
            if (res == null) {
                throw new IllegalStateException("Http request returned null response");
            }
            JSONObject jsonObject = JSONObject.parseObject(res);
            JSONObject data = jsonObject.getJSONObject("data");
            String authorID = data.getString("authorID");
            teacher.setAuthorID(authorID);
            userMapper.updateTeacher(teacher);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    @Override
    public void updateTeacher(Teacher teacher) {
        userMapper.updateTeacher(teacher);
    }

    @Transactional
    @Override
    public void deleteTeacher(Integer id) {
        userMapper.deleteStudentByTeacherId(id);
        userMapper.deleteTeacher(id);
    }

    @Override
    public List<Teacher> listTeacher() {
        return userMapper.listTeacher();
    }


}
