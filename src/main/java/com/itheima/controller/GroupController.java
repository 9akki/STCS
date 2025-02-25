package com.itheima.controller;

import com.itheima.pojo.Group;
import com.itheima.pojo.Result;
import com.itheima.service.GroupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping("/groups")
@RestController
public class GroupController {

    @Autowired
    private GroupService groupService;

    //根据学生id列表，以及小组信息，创建小组
    @PostMapping
    public Result add(Integer[] idList, @RequestBody Group group){
        log.info("根据学生id列表，以及小组信息，创建小组：学生id列表:{}，小组信息：{}",idList,group);
        //判断学生id列表是否为空，但是好像没什么意义，前端不允许不传id就访问该资源
        groupService.createGroup(idList,group);
        return Result.success();
    }

    //根据班级id查询小组列表
    @GetMapping("/class/{id}")
    public Result listByClassId(@PathVariable Integer id){
        log.info("根据班级id查询小组列表，班级id为：{}",id);
        return Result.success(groupService.listByClassId(id));
    }
    //根据小组id查询小组信息
    @GetMapping("/{id}")
    public Result single(@PathVariable Integer id){
        log.info("根据小组id查询小组信息，小组id为：{}",id);
        return Result.success(groupService.single(id));
    }
    //根据小组id删除小组
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id){
        log.info("根据小组id删除小组，小组id为：{}",id);
        groupService.delete(id);
        return Result.success();
    }
    //通过班级id和小组id，获取目前小组id为空或已选择本小组的学生列表
    @GetMapping("/empty")
    public Result empty(Integer classId,Integer groupId){
        return Result.success(groupService.empty(classId,groupId));
    }
    //根据小组id修改小组信息，包括修改小组成员
    @PutMapping("/update")
    public Result update(Integer[] idList,@RequestBody Group group){
        groupService.update(idList,group);
        return Result.success();
    }
}
