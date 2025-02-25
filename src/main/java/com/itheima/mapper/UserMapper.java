package com.itheima.mapper;

import com.itheima.pojo.Admin;
import com.itheima.pojo.Student;
import com.itheima.pojo.Teacher;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

    //根据学生的账号密码获取学生信息
    @Select("select * from student_collaborative_task_system.student where username = #{username} and password = #{password}")
    Student getStudentByUsernameAndPassword(String username, String password);
    //根据老师的账号密码获取老师信息
    @Select("select * from student_collaborative_task_system.teacher where username = #{username} and password = #{password}")
    Teacher getTeacherByUsernameAndPassword(String username, String password);

    @Select("select * from student_collaborative_task_system.student where id = #{id}")
    Student getStudentById(Integer id);

    List<Student> page(String name, Short gender, String classname, String groupname, Integer teacherID,Integer classID);

    @Delete("update student_collaborative_task_system.student set isdelete=1 where id=#{id}")
    void deleteById(Integer id);

    void update(Student student);

    Boolean add(Student student);

    @Select("select * from student_collaborative_task_system.student where class_id = #{id} and isdelete = 0 ")
    List<Student> getStudentByclassId(Integer id);

    @Select("select * from student_collaborative_task_system.student where group_id = #{id} and isdelete = 0 ")
    List<Student> getStudentBygroupdId(Integer id);

    //将学生的groupId字段置为null
    @Update("update student_collaborative_task_system.student set group_id = null where id = #{id}")
    void groupIdToNull(Integer id);

    @Update("update student_collaborative_task_system.student set class_id = null where id = #{id}")
    void classIdToNull(Integer id);

    @Select("select count(*) from student_collaborative_task_system.they_cant_vote where procedure_id = #{procedureID} and user_id = #{userID}")
    int canHeVote(Integer procedureID, Integer userID);

    @Delete("delete from student_collaborative_task_system.they_cant_vote where procedure_id = #{procedureID}")
    void deleteTheyContVote(Integer procedureID);

    @Select("select count(*) from student_collaborative_task_system.they_cant_go_next where task_id=#{taskID} and user_id = #{userID}")
    int canTheyGoNext(Integer taskID, Integer userID);

    @Select("select count(*) from student_collaborative_task_system.student where username = #{username}")
    Integer getStudentByUsername(String username);

    @Select("select count(*) from student_collaborative_task_system.teacher where id = #{teacherID}")
    Integer getTeacherByID(Integer teacherID);

    @Select("select * from student_collaborative_task_system.student where author_id = #{authorID}")
    Student getStudentByAuthorID(String authorID);

    @Select("select * from student_collaborative_task_system.teacher where id = #{id}")
    Teacher getTeacherById(Integer id);

    @Select("select * from student_collaborative_task_system.admin where username = #{username} and password = #{password}")
    Admin getAdminByUsernameAndPassword(String username, String password);

    Integer getTeacherByUsername(String username);

    void addTeacher(Teacher teacher);

    void updateTeacher(Teacher teacher);

    @Delete("update student_collaborative_task_system.student set isdelete=1 where teacher_id = #{id}")
    void deleteStudentByTeacherId(Integer id);

    @Delete("UPDATE student_collaborative_task_system.teacher set isdelete=1 where id = #{id}")
    void deleteTeacher(Integer id);

    @Select("select * from student_collaborative_task_system.teacher where isdelete = 0")
    List<Teacher> listTeacher();
}
