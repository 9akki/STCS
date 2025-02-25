package com.itheima.controller;

import com.google.gson.JsonObject;
import com.itheima.pojo.*;
import com.itheima.service.TaskService;
import com.itheima.utils.JSONUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping("/createTask")
    //*前端请求后，我们判断”task“即这个请求体内部是否为null值，即{}，如果为null，只按照”groupIDList“这个参数执行“修改小组的可查看属性”* 所以要另外写一个接口，修改小组的可查看属性
    //不行，因为如果要修改小组的可查看属性，需要接收到前端传来的“task”参数中的”taskID“，但是前端传来的“task”参数为null，所以无法修改小组的可查看属性 ↑ 而创建不需要拿到taskID，因为此时并不存在task
    //如果接收到的“groupIDList“参数为null，则认为所有小组均可查看图表 这个思路没问题
    public Result createTask(@RequestBody Task task){
        return Result.success(taskService.createTask(task));
    }

    @PostMapping("/createQuiz")
    public Result createQuiz(@RequestBody List<QuizItem> itemList,Integer taskID){
        taskService.createQuiz(itemList,taskID);
        return Result.success();
    }

    @GetMapping("/changeTaskEndTime")
    public Result changeTaskEndTime(Integer taskID,@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")LocalDateTime endTime){
        taskService.changeTaskEndTime(taskID,endTime);
        return Result.success();
    }

    @GetMapping("/getTaskPage")
    public Result getTaskPage(@RequestParam(defaultValue = "1") Integer page,
                               @RequestParam(defaultValue = "10") Integer pageSize,
                               Integer teacherID,
                               Integer classID,
                               String className,
                               String topic,
                               Integer status){
        return Result.success(taskService.getTaskPage(page,pageSize,teacherID,classID,className,topic,status));
    }
//    @GetMapping("/TasksOfClass")
//    public Result getTaskList(Integer classID){
//        List<Task> taskList = taskService.getTaskList(classID);
//        return Result.success(taskList);
//    }
//    @GetMapping("/TaskOfTeacher")
//    public Result getTaskInfoOfTeacher(Integer teacherID){
//        List<Task> taskOfTeacher = taskService.getTaskOfTeacher(teacherID);
//        return Result.success(taskOfTeacher);
//    }

    @DeleteMapping("/DeleteTask")
    public Result deleteTask(Integer taskID){
        taskService.deleteTask(taskID);
        return Result.success();
    }

    @GetMapping("/getTaskInfoOfGroup")
    public Result getTaskInfo(Integer taskID,Integer groupID){
        TaskInfoOfGroup taskInfoOfGroup = taskService.getTaskInfo(taskID,groupID);
        return Result.success(taskInfoOfGroup);
    }

    @GetMapping("/getProcedureInfo")
    public Result getProcedureInfo(Integer taskID,Integer groupID,Integer procedureOrder){
        Procedure procedureInfo = taskService.getProcedureInfo(taskID,groupID,procedureOrder);
        return Result.success(procedureInfo);
    }

    //设置小组图表可见性
    @GetMapping("/setGroupReadable")
    public Result setGroupReadable(Integer taskID,Integer groupID,Integer isReadable){
        taskService.setGroupReadable(taskID,groupID,isReadable);
        return Result.success();
    }

    @PostMapping("/createAnnouncement")
    public Result createAnnocement(@RequestBody Announcement announcement){
        announcement.setCreateTime(LocalDateTime.now());
        announcement.setUpdateTime(LocalDateTime.now());
        taskService.createAnnouncement(announcement);
        return Result.success();
    }

    @GetMapping("/getAnnouncementList")
    public Result getAnnouncementList(Integer taskID,@RequestParam(required = false) Integer studentID){
        if (studentID == null){
            return Result.success(taskService.getAnnouncementList(taskID));
        }

        Integer readCount = taskService.getReadCount(taskID,studentID);
        if (readCount == null)
            readCount = 0;
        Integer announcementCount = taskService.getAnnouncementCount(taskID);
        if (announcementCount == null)
            announcementCount = 0;

        if (readCount.equals(announcementCount))
            return Result.success("没有新通知");

        List<Announcement> announcementList = taskService.getAnnouncementList(taskID);
        JsonObject result = new JsonObject();
        result.addProperty("newCount",announcementCount-readCount);
        result.addProperty("announcementList",JSONUtils.beanToJson(announcementList));
        return Result.success(result+"");
    }

    @PutMapping("/isRead")
    public Result isRead(Integer taskID,Integer studentID){
        taskService.isRead(taskID,studentID);
        return Result.success();
    }


    @GetMapping("/goNextProcedure")
    public Result goNextProcedure(Integer taskID,Integer groupID,Integer procedureTypePast,Integer procedureIDPast,Integer procedureNow){
        taskService.goNextProcedure(taskID,groupID,procedureTypePast,procedureIDPast,procedureNow);
        return Result.success();
    }

    //根据聊天室ID获取聊天记录
    @GetMapping("/getMessageRecord")
    public Result getMessageRecord(Integer chatRoomID){
        List<Message> messageRecordList = taskService.getMessageRecord(chatRoomID);
        return Result.success(messageRecordList);
    }

    //根据流程ID获取头脑风暴主张
        @GetMapping("/getMindStormMessage")
    public Result getBrainstorming(Integer procedureID){
        List<MindStormMessage> mindStormList = taskService.getMindStormList(procedureID);
        return Result.success(mindStormList);
    }

    //根据quizID获取quizItem列表
    @GetMapping("/getQuizItemList")
    public Result getQuizItemList(Integer quizID){
        List<QuizItem> quizItemList = taskService.getQuizItemList(quizID);
        return Result.success(quizItemList);
    }

    //保存学生的问卷项完成情况
    @PostMapping("/recordQuizItemScore")
    public Result getQuizResultList(Integer quizID,Integer studentID , @RequestBody List<QuizItemScore> quizItemScoreList){
        taskService.recordQuizItemScore(quizID,studentID,quizItemScoreList);
        return Result.success();
    }

    //获取学生对quizItem的打分情况
    @GetMapping("/getQuizItemScore")
    public Result getQuizItemScore(Integer quizID,Integer studentID){
        List<QuizItemScore> quizItemScoreList = taskService.getQuizItemScore(quizID,studentID);
        return Result.success(quizItemScoreList);
    }

    @GetMapping("/voteAgain")
    public Result voteAgain(Integer procedureID,Integer chatRoomID){
        taskService.voteAgain(procedureID,chatRoomID);
        return Result.success();
    }
}
