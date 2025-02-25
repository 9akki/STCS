package com.itheima.mapper;

import com.itheima.pojo.Classpojo;
import com.itheima.pojo.Student;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ClassMapper {

    Integer InsertClass(Classpojo classpojo);

    @Delete("update student_collaborative_task_system.class set isdelete = 1 where id = #{id}")
    void deleteClass(Integer id);

    @Select("select * from student_collaborative_task_system.class where id = #{id}")
    Classpojo getById(Integer id);

    @Select("select * from student_collaborative_task_system.class where teacher_id = #{id} and isdelete = 0")
    List<Classpojo> listByTeacherId(Integer id);

    void updateClass(Classpojo classpojo);

    @Select("select * from student_collaborative_task_system.student where class_id is null and teacher_id = #{teacherID}")
    List<Student> Empty(Integer teacherID);

    @Update("update student_collaborative_task_system.class set student_num = student_num + #{i} where id = #{classId}")
    void setClassNum(Integer classId, int i);
}
