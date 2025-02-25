package com.itheima.mapper;

import com.itheima.pojo.Group;
import com.itheima.pojo.Student;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface GroupMapper {

    Integer insert(Group group);

    @Select("select * from student_collaborative_task_system.student_group where class_id = #{id} and is_delete = 0")
    List<Group> listByClassId(Integer id);

    @Select("select * from student_collaborative_task_system.student_group where id = #{id}")
    Group single(Integer id);

    @Select("select id from student_collaborative_task_system.student_group where class_id = #{classId} and is_delete = 0")
    Integer[] getGroupId(Integer classId);

    @Delete("update student_collaborative_task_system.student_group set is_delete = 1 where id = #{id}")
    void delete(Integer id);

    @Select("select * from student_collaborative_task_system.student where class_id = #{classId} and (group_id is null or group_id = #{groupId}) and isdelete = 0")
    List<Student> empty(Integer classId, Integer groupId);

    void updateGroup(Group group);

    @Select("select group_id from student_collaborative_task_system.student_group where id = #{groupID}")
    String getEGroupId(Integer groupID);

    @Update("update student_collaborative_task_system.student_group set member_num = member_num + #{i} where id = #{groupId}")
    void setGroupNum(Integer groupId, int i);
}
