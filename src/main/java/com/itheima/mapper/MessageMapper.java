package com.itheima.mapper;

import com.itheima.pojo.GoNext;
import com.itheima.pojo.Message;
import com.itheima.pojo.MindStormMessage;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface MessageMapper {

    @Insert("insert into student_collaborative_task_system.message(sender, sender_id,chatroom_id, content, type, timestamp, sender_avatar,to_user,to_user_name) " +
            "values(#{sender},#{senderID},#{chatroomID},#{content},#{type},#{timeStamp},#{senderAvatar},#{toUser},#{toUserName})")
    void addMessage(Message messageObj);

    void addMindStormMessage(MindStormMessage mindStormObj);

    @Update("update student_collaborative_task_system.mindstorm_message set score = score + 1 where id = #{mindID}")
    void addScoreByMindID(int mindID);

    @Update("update student_collaborative_task_system.task_info_of_group set go_next = go_next + 1 where task_id = #{taskID} and group_id = #{groupID}")
    void increaseGoNext(GoNext goNext);

    @Select("select * from student_collaborative_task_system.message where chatroom_id = #{chatRoomID} order by timestamp asc")
    List<Message> getMessageRecord(Integer chatRoomID);

    @Insert("insert into student_collaborative_task_system.they_cant_vote(procedure_id, user_id) values(#{procedureID},#{userID})")
    void addCanThem(int procedureID, int userID);

    @Insert("insert into student_collaborative_task_system.they_cant_go_next(task_id, group_id, user_id) values(#{taskID},#{groupID},#{userID})")
    void addToTheyCantGoNext(GoNext goNext);
}
