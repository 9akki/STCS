package com.itheima.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.itheima.config.EtherpadConfig;
import com.itheima.mapper.GroupMapper;
import com.itheima.mapper.UserMapper;
import com.itheima.pojo.Group;
import com.itheima.pojo.Student;
import com.itheima.service.GroupService;
import com.itheima.utils.HttpUtils;
import com.itheima.utils.IsInUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
public class GroupServiceImpl implements GroupService {

    @Autowired
    private GroupMapper groupMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private EtherpadConfig etherpadConfig;

    @Transactional
    @Override
    public void createGroup(Integer[] idList, Group group) {
        group.setCreateTime(LocalDateTime.now());
        group.setUpdateTime(LocalDateTime.now());

        if (idList!=null)
        group.setMemberNum(idList.length);
        else group.setMemberNum(0);

        groupMapper.insert(group);

        if (idList != null && idList.length>0) {
            Integer gId = group.getId();
            Student student = new Student();
            student.setGroupId(gId);
            for (Integer id : idList) {
                student.setId(id);
                userMapper.update(student);
            }
        }

        HashMap<String,String> hm = new HashMap<>();
        hm.put("apikey",etherpadConfig.getApikey());
        System.out.println(etherpadConfig.getEtherpadUrl()+etherpadConfig.getCreateGroupIfNotExistsFor());
        hm.put("groupMapper", String.valueOf(group.getId()));

        String res = HttpUtils.get(etherpadConfig.getEtherpadUrl()+etherpadConfig.getCreateGroupIfNotExistsFor(),hm);
        if (res == null) {
            throw new IllegalStateException("Http request returned null response");
        }
        System.out.println(res);
        JSONObject jsonObject = JSONObject.parseObject(res);
        JSONObject data = jsonObject.getJSONObject("data");
        String groupID = data.getString("groupID");

        group.setGroupID(groupID);
        groupMapper.updateGroup(group);
    }

    @Override
    public List<Group> listByClassId(Integer id) {
        List<Group> groupList = groupMapper.listByClassId(id);
        return groupList;
    }

    @Override
    public Group single(Integer id) {
        Group group = groupMapper.single(id);
        return group;
    }

    @Override
    public void delete(Integer id) {
        List<Student> studentList = userMapper.getStudentBygroupdId(id);
        for (Student student : studentList) {
            userMapper.groupIdToNull(student.getId());
        }
        groupMapper.delete(id);
    }

    @Override
    public List<Student> empty(Integer classId, Integer groupId) {
        List<Student> empty = groupMapper.empty(classId,groupId);
        return empty;
    }

    @Override
    public void update(Integer[] idList, Group group) {
        //获取原来小组成员
        List<Student> studentList = userMapper.getStudentBygroupdId(group.getId());
        List<Integer> listId = new ArrayList<>();
        Student student = new Student();
        //如果传进的名单不为null，则按照名单修改小组成员
        if(idList!=null&&idList.length!=0) {
            group.setMemberNum(idList.length);
            student.setGroupId(group.getId());
            for (Student studentItem : studentList) {
                listId.add(studentItem.getId());
            }
            //将更新前在组内，更新后不在组内的成员的小组id字段置为null
            for (Integer id : listId) {
                if (!IsInUtils.isIn(id, Arrays.asList(idList)))
                    userMapper.groupIdToNull(id);
            }
            //遍历前端传来的成员名单，并通过id将该名单内的所有成员的小组id字段置为传进小组的id
            for (Integer id : idList) {
                student.setId(id);
                userMapper.update(student);
            }
        }
        else {
            for (Student studentItem : studentList){
                userMapper.groupIdToNull(studentItem.getId());
            }
            group.setMemberNum(0);
        }

        group.setUpdateTime(LocalDateTime.now());
        //修改小组的基本信息
        groupMapper.updateGroup(group);
    }

}
