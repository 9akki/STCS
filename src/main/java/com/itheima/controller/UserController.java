package com.itheima.controller;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.support.hsf.HSFJSONUtils;
import com.itheima.pojo.*;
import com.itheima.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    //根据id获取学生信息
    @GetMapping("/{id}")
    public Result getStudentById(@PathVariable Integer id){
        Student student = userService.getStudentById(id);
        return Result.success(student);
    }
    //根据id获取老师信息
    @GetMapping("/teacher/{id}")
    public Result getTeacherById(@PathVariable Integer id){
        Teacher teacher = userService.getTeacherById(id);
        return Result.success(teacher);
    }

    //分页条件查询，需要过滤被删除学生
    @GetMapping
    public Result page(@RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer pageSize,
                       String name,
                       String classname,
                       String groupname,
                       Short gender,
                       Integer teacherID,
                       Integer classID) {
        log.info("带条件的分页复杂查询,参数:{},{},{},{},{},{},{}",name, gender,page,pageSize,classname,groupname,teacherID,classID);
        UsersResult result = userService.page(name, gender,page,pageSize,classname,groupname,teacherID,classID);
        return Result.success(result);
    }

    //根据id删除学生(软删除)
    @DeleteMapping("/{id}")
    public Result deleteById(@PathVariable Integer id){
        userService.deleteById(id);
        return Result.success();
    }

    //根据学生id修改学生信息（配合查询回显）
    @PutMapping
    public Result update(@RequestBody Student student){
        userService.update(student);
        return Result.success();
    }
    //添加学生
    @PostMapping
    public Result add(@RequestBody Student student){
        if(userService.add(student))
        return Result.success();
        else return Result.error("添加失败,可能学生已存在");
    }
    //添加老师
    @PostMapping("/teacher")
    public Result addTeacher(@RequestBody Teacher teacher){
        if(userService.addTeacher(teacher))
            return Result.success();
        else return Result.error("添加失败,可能老师已存在");
    }
    //修改老师信息
    @PutMapping("/teacher")
    public Result updateTeacher(@RequestBody Teacher teacher){
        userService.updateTeacher(teacher);
        return Result.success();
    }
    //删除老师及相关小组和任务
    @DeleteMapping("/teacher")
    public Result deleteTeacher(Integer id){
        userService.deleteTeacher(id);
        return Result.success();
    }
    //获取老师信息列表
    @GetMapping("/teacher")
    public Result listTeacher(){
        List<Teacher> teacherList = userService.listTeacher();
        return Result.success(teacherList);
    }
    //已废弃
    //根据班级id获取学生信息列表，需要过滤被删除学生
    @GetMapping("/class/{id}")
    public Result getStudentByclassId(@PathVariable Integer id){
        List<Student> studentList = userService.getStudentByclassId(id);
        return Result.success(studentList);
    }

    //根据小组id获取学生信息列表，需要过滤被删除学生
    @GetMapping("/group/{id}")
    public Result getStudentBygroupId(@PathVariable Integer id){
        List<Student> studentList = userService.getStudentBygroupdId(id);
        return Result.success(studentList);
    }

    @GetMapping("/canHeVote")
    public Result canHeVote(Integer procedureID, Integer userID){
        return Result.success(userService.canHeVote(procedureID, userID));
    }

    @GetMapping("/canTheyGoNext")
    public Result canTheyGoNext(Integer taskID, Integer userID){
        return Result.success(userService.canTheyGoNext(taskID, userID));
    }

    @GetMapping("/createEtherpadSession")
    public Result createEtherpadSession(String authorID, String etherpadGroupID){
        return Result.success(userService.createEtherpadSession(authorID, etherpadGroupID));
    }
}
