package com.itheima.service;

import com.itheima.pojo.*;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;
import java.util.List;

public interface TaskService {
    Integer createTask(Task task);

    void createQuiz(List<QuizItem> itemList, Integer taskID);

    List<Task> getTaskList(Integer classID);

    TaskInfoOfGroup getTaskInfo(Integer taskID, Integer groupID);

    Procedure getProcedureInfo(Integer taskID, Integer groupID, Integer procedureOrder);

    void createAnnouncement(Announcement announcement);

    List<Announcement> getAnnouncementList(Integer taskID);

    void goNextProcedure(Integer taskID, Integer groupID, Integer procedureTypePast, Integer procedureIDPast,Integer procedureNow);

    List<Message> getMessageRecord(Integer chatRoomID);

    List<MindStormMessage> getMindStormList(Integer procedureID);

    List<QuizItem> getQuizItemList(Integer quizID);

    void recordQuizItemScore(Integer quizID, Integer studentID, List<QuizItemScore> quizItemScoreList);

    List<QuizItemScore> getQuizItemScore(Integer quizID, Integer studentID);

    List<Task> getTaskOfTeacher(Integer teacherID);

    void voteAgain(Integer procedureID,Integer chatRoomID);


    TaskResult getTaskPage(Integer page, Integer pageSize, Integer teacherID, Integer classID, String className, String topic,Integer status);

    void deleteTask(Integer taskID);


    void changeTaskEndTime(Integer taskID, LocalDateTime endTime);

    void setGroupReadable(Integer taskID, Integer groupID, Integer isReadable);

    Task getTaskById(Integer taskID);

    List<Integer> getCollabrotiveIdList(Integer taskId, Integer groupId);

    CollaborativeProcedure getCollabrotiveProcedureInfo(Integer procedureId);

    Integer getReadCount(Integer taskID, Integer studentID);

    Integer getAnnouncementCount(Integer taskID);

    void isRead(Integer taskID, Integer studentID);
}
