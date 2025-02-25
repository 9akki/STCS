package com.itheima.controller;


import com.itheima.pojo.Classpojo;
import com.itheima.pojo.Result;
import com.itheima.service.ClassService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/classes")
public class ClassController {

    @Autowired
    private ClassService classService;

    //添加班级
    @PostMapping
    public Result addClass(@RequestBody Classpojo classpojo, Integer[] idList){
        log.info("添加班级:{}",classpojo);
        classService.addClass(classpojo,idList);
        return Result.success();
    }

    //根据id删除班级
    @DeleteMapping
    public Result deleteClass(Integer id){
        log.info("删除班级:{}",id);
        classService.deleteClass(id);
        return Result.success();
    }

    //根据班级id获取班级信息
    @GetMapping("/{id}")
    public Result single(@PathVariable Integer id){
        log.info("根据id查询班级信息");
        Classpojo classpojo = classService.single(id);
        return Result.success(classpojo);
    }

    //根据教师id获取班级信息列表
    @GetMapping("/teacher/{id}")
    public Result listByTeacherId(@PathVariable Integer id){
        log.info("根据教师id查询班级信息列表");
        return Result.success(classService.listByTeacherId(id));
    }

    @PutMapping
    public Result updateClass(@RequestBody Classpojo classpojo){
        log.info("修改班级信息:{}",classpojo);
        classService.updateClass(classpojo);
        return Result.success();
    }

    //获取所有班级id为null的学生信息
    @GetMapping("/empty")
    public Result Empty(Integer teacherID){
        return Result.success(classService.Empty(teacherID));
    }
}
