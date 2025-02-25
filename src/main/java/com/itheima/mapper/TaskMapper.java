package com.itheima.mapper;

import com.itheima.pojo.*;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface TaskMapper {


    Integer createCollaborativeProcedure(CollaborativeProcedure collaborativeProcedure);

    Integer createTask(Task task);

    String getProcedureOrderList(Integer id);

    Integer createChatroom(Chatroom chatroom);

    @Insert("insert into student_collaborative_task_system.task_info_of_group(task_id, group_id, chatroom_id,  create_time, update_time) values(#{id},#{groupID},#{chatroomID},#{createTime},#{updateTime})")
    void createTaskInfoOfGroup(Integer groupID, Integer id, Integer chatroomID, LocalDateTime createTime, LocalDateTime updateTime);

    void updateCollaborativeProcedure(Integer ID, String padID, String readOnlyID, LocalDateTime updateTime,LocalDateTime endTime);

    void createProcedureCategory(Integer taskID, Integer groupID, Integer procedureOrderInTask, Integer procedureType, Integer collaborativeProcedureID, LocalDateTime createTime, LocalDateTime updateTime);

    void createMindStormProcedure(MindStormProcedure mindStormProcedure);

    void createQuiz(Quiz quiz);

    @Insert("insert into student_collaborative_task_system.quiz_item(quiz_id, item_order,content) values(#{quizID},#{itemOrder},#{content})")
    void createQuizItem(QuizItem item);

    @Select("select id from student_collaborative_task_system.quiz where task_id = #{taskID}")
    Integer findQuizIDByTaskID(Integer taskID);

    void createReflectionProcedure(ReflectionProcedure reflectionProcedure);

    @Select("select * from student_collaborative_task_system.task where class_id = #{classID}")
    List<Task> getTaskListByClassID(Integer classID);

    @Select("select * from student_collaborative_task_system.task_info_of_group where task_id = #{taskID} and group_id = #{groupID}")
    TaskInfoOfGroup getTaskInfo(Integer taskID, Integer groupID);

    @Select("select procedure_type,procedure_id from student_collaborative_task_system.procedure_category where task_id = #{taskID} and group_id = #{groupID} and procedure_order = #{procedureOrder}")
    CategoryResult getProcedureCategoryInfo(Integer taskID, Integer groupID, Integer procedureOrder);

    @Select("select * from student_collaborative_task_system.procedure_mindstorm where id = #{procedureID}")
    MindStormProcedure getMindStormProcedureInfo(Integer procedureID);

    @Select("select * from student_collaborative_task_system.procedure_collaborative where id = #{procedureID}")
    CollaborativeProcedure getCollaborativeProcedureInfo(Integer procedureID);

    @Select("select * from student_collaborative_task_system.procedure_reflection where id = #{procedureID}")
    ReflectionProcedure getReflectionProcedureInfo(Integer procedureID);

    @Insert("insert into student_collaborative_task_system.announcement(task_id, content, create_time, update_time) values(#{taskID},#{content},#{createTime},#{updateTime})")
    void addAnnouncement(Announcement announcement);

    @Select("select * from student_collaborative_task_system.announcement where task_id = #{taskID}")
    List<Announcement> getAnnouncementList(Integer taskID);

    @Update("update student_collaborative_task_system.procedure_mindstorm set end_time = now(), update_time = now() where id = #{procedureIDPast}")
    void updateMindStormProcedure(Integer procedureIDPast);

    @Update("update student_collaborative_task_system.procedure_reflection set end_time = now(), update_time = now() where id = #{procedureIDPast}")
    void updateReflectionProcedure(Integer procedureIDPast);

    // 根据任务ID获取任务信息
    @Select("select * from student_collaborative_task_system.task where id = #{taskID}")
    Task getTask(Integer taskID);

    void updateTaskInfoOfGroup(Integer taskID,Integer groupID,Integer procedureNow,LocalDateTime updateTime,LocalDateTime endTime);

    @Select("select * from student_collaborative_task_system.mindstorm_message where procedure_id = #{procedureID}")
    List<MindStormMessage> getMindStormList(Integer procedureID);

    @Select("select * from student_collaborative_task_system.quiz_item where quiz_id = #{quizID} order BY item_order")
    List<QuizItem> getQuizItemList(Integer quizID);

    @Insert("insert into student_collaborative_task_system.quiz_item_score(quiz_id, student_id, score, quiz_item_id) values(#{quizID},#{studentID},#{score},#{quizItemID})")
    void recordQuizItemScore(QuizItemScore quizItemScore);

    @Select("select * from student_collaborative_task_system.quiz_item_score where quiz_id = #{quizID} and student_id = #{studentID} order by quiz_item_id")
    List<QuizItemScore> getQuizItemScore(Integer quizID, Integer studentID);

    @Select("select pad_id from student_collaborative_task_system.procedure_collaborative where id = #{procedureID}")
    String getPadIDByProcedureID(Integer procedureID);

    @Delete("delete from student_collaborative_task_system.they_cant_go_next where group_id = #{groupID} and task_id = #{taskID}")
    void deleteTheyCantGoNext(Integer groupID, Integer taskID);

    List<Task> getTaskPage(Integer teacherID, Integer classID, String className, String topic, Integer status);

    @Update("update student_collaborative_task_system.task set is_delete = 1 where id = #{taskID}")
    void deleteTask(Integer taskID);

    @Update("update student_collaborative_task_system.task_info_of_group set readable = #{isReadable} where task_id = #{taskID} and group_id = #{groupID}")
    void setGroupReadable(Integer taskID, Integer groupID, Integer isReadable);

    @Update("update student_collaborative_task_system.task set end_time = #{endTime} where id = #{taskID}")
    void changeTaskEndTime(Integer taskID, LocalDateTime endTime);

    void deleteQuizItemScore(Integer quizID, Integer studentID);

    @Select("select * from student_collaborative_task_system.task where id = #{taskID}")
    Task getTaskById(Integer taskID);

    List<Integer> getCollabrotiveIdList(Integer taskId, Integer groupId);

    CollaborativeProcedure getCollabrotiveProcedureInfo(Integer procedureId);

    @Select("select read_count from student_collaborative_task_system.is_read where student_id = #{studentID} and task_id = #{taskID}")
    Integer getReadCount(Integer taskID, Integer studentID);

    @Select("select count(*) from student_collaborative_task_system.announcement where task_id = #{taskID}")
    Integer getAnnouncementCount(Integer taskID);

    @Update("update student_collaborative_task_system.is_read set read_count = #{announcementCount} where student_id = #{studentID}")
    void isRead(Integer studentID, Integer announcementCount);

    @Select("select count(*) from student_collaborative_task_system.is_read where  student_id = #{studentID} and task_id = #{taskID}")
    Integer isReadbefore(Integer studentID, Integer taskID);

    @Insert("insert into student_collaborative_task_system.is_read(student_id, task_id, read_count) values(#{studentID},#{taskID},#{announcementCount})")
    void addIsRead(Integer studentID, Integer taskID, Integer announcementCount);
}
