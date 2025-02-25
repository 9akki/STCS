package com.itheima.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.itheima.config.EtherpadConfig;
import com.itheima.mapper.*;
import com.itheima.pojo.*;
import com.itheima.server.WebSocketServer;
import com.itheima.service.TaskService;
import com.itheima.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.websocket.Session;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Stream;

@Slf4j
@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private GroupMapper groupMapper;
    @Autowired
    private EtherpadConfig etherpadConfig;
    @Autowired
    private MessageMapper messageMapper;
    @Autowired
    private ClassMapper classMapper;
    @Autowired
    private UserMapper userMapper;

    @Transactional
    @Override
    public Integer createTask(Task task) {
        //先创建任务，再根据任务id和小组id创建每个小组的第一个流程，创建流程部分功能要解耦
        task.setCreateTime(LocalDateTime.now());
        task.setUpdateTime(LocalDateTime.now());
        taskMapper.createTask(task);

        //拿出procedrueOrderList，将其转换为数组，并取出其中的第order个元素(将这个步骤提取成一个方法，后续会多次将该字段由字符串转数组，order代表查询的是任务的第几个流程，目前流程是procedure_now)
        //Integer getProceduresType(taskID,order),返回值即为流程类型，然后去创建该相应类型的流程即可
        Integer procedureType = getProceduresType(task.getProcedureOrderList(), 0);
        //通过班级id拿到该班所有小组的id列表
        Integer[] groupIDs = groupMapper.getGroupId(task.getClassID());
        Chatroom chatroom = new Chatroom();
        //循环创建每个小组的初始流程
        for (Integer groupID : groupIDs) {
            chatroom.setCreateTime(LocalDateTime.now());
            chatroom.setUpdateTime(LocalDateTime.now());
            taskMapper.createChatroom(chatroom);
            taskMapper.createTaskInfoOfGroup(groupID, task.getId(), chatroom.getId(), LocalDateTime.now(), LocalDateTime.now());
            //怎么确保正常回滚呢？前几次测试都没有完全回滚，事务方面的处理还需要加强学习
            createProcedure(procedureType, groupID, task.getId(), 0);
        }
        //根据参数groupIDList设置小组是否可以查看图表，先不管
        //是不是换成另外一个接口更好？这样老师可以灵活地改变哪些小组可以查看 (采用）
        return task.getId();
    }

    @Transactional
    @Override
    public void createQuiz(List<QuizItem> itemList, Integer taskID) {
        Quiz quiz = new Quiz();
        quiz.setCreateTime(LocalDateTime.now());
        quiz.setUpdateTime(LocalDateTime.now());
        quiz.setTaskID(taskID);
        taskMapper.createQuiz(quiz);
        for (QuizItem item : itemList) {
            item.setQuizID(quiz.getId());
            taskMapper.createQuizItem(item);
        }
    }

    @Override
    public List<Task> getTaskList(Integer classID) {
        List<Task> taskList = taskMapper.getTaskListByClassID(classID);
        return taskList;
    }

    @Override
    public TaskInfoOfGroup getTaskInfo(Integer taskID, Integer groupID) {
        TaskInfoOfGroup taskInfo = taskMapper.getTaskInfo(taskID, groupID);
        return taskInfo;
    }

    @Override
    public Procedure getProcedureInfo(Integer taskID, Integer groupID, Integer procedureOrder) {
        CategoryResult result = taskMapper.getProcedureCategoryInfo(taskID, groupID, procedureOrder);
        switch (result.getProcedureType()) {
            case 1: {
                MindStormProcedure mindStormProcedure = taskMapper.getMindStormProcedureInfo(result.getProcedureID());
                return mindStormProcedure;
            }
            case 2: {
                CollaborativeProcedure collaborativeProcedure = taskMapper.getCollaborativeProcedureInfo(result.getProcedureID());
                return collaborativeProcedure;
            }
            case 3: {
                ReflectionProcedure reflectionProcedure = taskMapper.getReflectionProcedureInfo(result.getProcedureID());
                return reflectionProcedure;
            }
            default:
                return null;
        }
    }

    @Override
    public void createAnnouncement(Announcement announcement) {
        taskMapper.addAnnouncement(announcement);
    }

    @Override
    public List<Announcement> getAnnouncementList(Integer taskID) {
        List<Announcement> announcementList = taskMapper.getAnnouncementList(taskID);
        return announcementList;
    }

    @Transactional
    @Override
    public void goNextProcedure(Integer taskID, Integer groupID, Integer procedureTypePast, Integer procedureIDPast, Integer procedureNow) {
        //将前一流程的结束时间设为此刻
        switch (procedureTypePast){
            case 1://头脑风暴流程
            {
                taskMapper.updateMindStormProcedure(procedureIDPast);
                break;
            }
            case 2://多人协作流程
            {
                taskMapper.updateCollaborativeProcedure(procedureIDPast,null,null,LocalDateTime.now(),LocalDateTime.now());
                break;
            }
            case 3://反思流程
            {
                taskMapper.updateReflectionProcedure(procedureIDPast);
                break;
            }
        }
        //通过任务ID拿到流程顺序数组，拿到流程类型后，为该小组创建该流程
        Task task = taskMapper.getTask(taskID);
        try{
            Integer procedureType = getProceduresType(task.getProcedureOrderList(), procedureNow+1);
            createProcedure(procedureType, groupID, taskID, procedureNow+1);
            //完成后将(task_info_of_group)中的procedureNow+1
            taskMapper.updateTaskInfoOfGroup(taskID,groupID,procedureNow+1,LocalDateTime.now(),null);
        }catch (Exception e){
            //完成后将(task_info_of_group)中的procedureNow+1
            taskMapper.updateTaskInfoOfGroup(taskID,groupID,procedureNow,LocalDateTime.now(),LocalDateTime.now());
        }finally {
            taskMapper.deleteTheyCantGoNext(groupID,taskID);
        }

    }

    @Override
    public List<Message> getMessageRecord(Integer chatRoomID) {
        List<Message> messageList = messageMapper.getMessageRecord(chatRoomID);
        return messageList;
    }

    @Override
    public List<MindStormMessage> getMindStormList(Integer procedureID) {
        List<MindStormMessage> mindStormMessageList = taskMapper.getMindStormList(procedureID);
        return mindStormMessageList;
    }

    @Override
    public List<QuizItem> getQuizItemList(Integer quizID) {
        List<QuizItem> quizItemList = taskMapper.getQuizItemList(quizID);
        return quizItemList;
    }

    @Transactional
    @Override
    public void recordQuizItemScore(Integer quizID, Integer studentID, List<QuizItemScore> quizItemScoreList) {
        //先删除旧记录再保存新记录
        taskMapper.deleteQuizItemScore(quizID,studentID);
        for (QuizItemScore quizItemScore : quizItemScoreList) {
            quizItemScore.setQuizID(quizID);
            quizItemScore.setStudentID(studentID);
            taskMapper.recordQuizItemScore(quizItemScore);
        }
    }

    @Override
    public List<QuizItemScore> getQuizItemScore(Integer quizID, Integer studentID) {
        List<QuizItemScore> quizItemScoreList = taskMapper.getQuizItemScore(quizID, studentID);
        return quizItemScoreList;
    }

    @Override
    public List<Task> getTaskOfTeacher(Integer teacherID) {
        List<Classpojo> classIDList = classMapper.listByTeacherId(teacherID);
        List<Task> taskList = new ArrayList<>();
        for (Classpojo classpojo : classIDList) {
            List<Task> taskListSub = taskMapper.getTaskListByClassID(classpojo.getId());
            taskList.addAll(taskListSub);
        }
        return taskList;
    }

    @Override
    public void voteAgain(Integer procedureID, Integer chatRoomID) {
        userMapper.deleteTheyContVote(procedureID);
        for (Map.Entry<Session, Integer> entry : WebSocketServer.sessionToChatRoom.entrySet()) {
            if (entry.getValue() == chatRoomID) {
                JSONObject json = new JSONObject();
                json.put("type", "voteAgain");
                json.put("procedureID", procedureID);
                try {
                    entry.getKey().getBasicRemote().sendText(json.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public TaskResult getTaskPage(Integer page, Integer pageSize, Integer teacherID, Integer classID, String className, String topic, Integer status) {
        PageHelper.startPage(page, pageSize);
        List<Task> taskList = taskMapper.getTaskPage(teacherID, classID, className, topic, status);
        List<String> classNameList = new ArrayList<>();

        for (Task task : taskList) {
            if (classMapper.getById(task.getClassID())!=null)
                classNameList.add(classMapper.getById(task.getClassID()).getName());
            else classNameList.add(null);
        }

        Page<Task> p = (Page<Task>) taskList;
        TaskPageBean taskPageBean = new TaskPageBean(p.getTotal(),p.getResult());
        TaskResult taskResult = new TaskResult(taskPageBean, classNameList);

        return taskResult;
    }

    @Override
    public void deleteTask(Integer taskID) {
        taskMapper.deleteTask(taskID);
    }

    @Override
    public void setGroupReadable(Integer taskID, Integer groupID, Integer isReadable) {
        taskMapper.setGroupReadable(taskID, groupID, isReadable);
    }

    @Override
    public Task getTaskById(Integer taskID) {
        return taskMapper.getTaskById(taskID);
    }

    @Override
    public List<Integer> getCollabrotiveIdList(Integer taskId, Integer groupId) {
        return taskMapper.getCollabrotiveIdList(taskId, groupId);
    }

    @Override
    public CollaborativeProcedure getCollabrotiveProcedureInfo(Integer procedureId) {
        return taskMapper.getCollabrotiveProcedureInfo(procedureId);
    }

    @Override
    public Integer getReadCount(Integer taskID, Integer studentID) {
        Integer readCount = taskMapper.getReadCount(taskID, studentID);
        return readCount;
    }

    @Override
    public Integer getAnnouncementCount(Integer taskID) {
        Integer announcementCount = taskMapper.getAnnouncementCount(taskID);
        return announcementCount;
    }

    @Override
    public void isRead(Integer taskID, Integer studentID) {
        if(!taskMapper.isReadbefore(studentID, taskID).equals(0))
            taskMapper.isRead(studentID, taskMapper.getAnnouncementCount(taskID));
        else
            taskMapper.addIsRead(studentID, taskID, taskMapper.getAnnouncementCount(taskID));
    }

    @Override
    public void changeTaskEndTime(Integer taskID, LocalDateTime endTime) {
        taskMapper.changeTaskEndTime(taskID, endTime);
    }


    @Transactional
    public void createProcedure(Integer procedureType, Integer groupID, Integer taskID, Integer procedureOrderInTask) {
        switch (procedureType) {
            case 1://创建头脑风暴流程
            {
                MindStormProcedure mindStormProcedure = new MindStormProcedure();
                mindStormProcedure.setCreateTime(LocalDateTime.now());
                mindStormProcedure.setUpdateTime(LocalDateTime.now());
                taskMapper.createMindStormProcedure(mindStormProcedure);
                taskMapper.createProcedureCategory(taskID, groupID, procedureOrderInTask, procedureType, mindStormProcedure.getId(), LocalDateTime.now(), LocalDateTime.now());
                break;
            }
            case 2://创建多人协作流程
            {
                Group group = groupMapper.single(groupID);
                CollaborativeProcedure collaborativeProcedure = new CollaborativeProcedure();
                collaborativeProcedure.setCreateTime(LocalDateTime.now());
                collaborativeProcedure.setUpdateTime(LocalDateTime.now());
                taskMapper.createCollaborativeProcedure(collaborativeProcedure);
                //发送http请求的代码应该抽取为方法
                HashMap<String, String> paramMap1 = new HashMap<>();
                paramMap1.put("apikey", etherpadConfig.getApikey());
                paramMap1.put("groupID", group.getGroupID());
                paramMap1.put("padName", String.valueOf(collaborativeProcedure.getId()) + "test");

                String res1 = HttpUtils.get(etherpadConfig.getEtherpadUrl() + etherpadConfig.getCreateGroupPad(), paramMap1);
                if (res1 == null) {
                    throw new IllegalStateException("Http request returned null response");
                }
                JSONObject jsonObject1 = JSONObject.parseObject(res1);
                String code1 = jsonObject1.getString("code");
                //这样做是否能在etherpad创建小组pad失败时回滚事务呢？
                if (!"0".equals(code1)) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                }

                collaborativeProcedure.setPadID(group.getGroupID() + "$" + collaborativeProcedure.getId() + "test");

                //发送http请求的代码应该抽取为方法
                HashMap<String, String> paramMap2 = new HashMap<>();
                paramMap2.put("apikey", etherpadConfig.getApikey());
                paramMap2.put("padID", group.getGroupID() + "$" + collaborativeProcedure.getId() + "test");


                String res2 = HttpUtils.get(etherpadConfig.getEtherpadUrl() + etherpadConfig.getGetReadOnlyID(), paramMap2);
                if (res2 == null) {
                    throw new IllegalStateException("Http request returned null response");
                }
                JSONObject jsonObject2 = JSONObject.parseObject(res2);
                String code2 = jsonObject2.getString("code");
                //这样做是否能在etherpad创建readOnlypad失败时回滚事务呢？
                if (!"0".equals(code2)) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                }

                JSONObject data = jsonObject2.getJSONObject("data");

                collaborativeProcedure.setReadonlyID(data.getString("readOnlyID"));

                taskMapper.updateCollaborativeProcedure(collaborativeProcedure.getId(), collaborativeProcedure.getPadID(), collaborativeProcedure.getReadonlyID(), LocalDateTime.now(), null);

                //最后将该流程的信息加入分类表中，这样才能定位到该流程
                taskMapper.createProcedureCategory(taskID, groupID, procedureOrderInTask, procedureType, collaborativeProcedure.getId(), LocalDateTime.now(), LocalDateTime.now());
                break;
            }
            case 3://创建反思流程
            {
                Integer quizID = taskMapper.findQuizIDByTaskID(taskID);
                ReflectionProcedure reflectionProcedure = new ReflectionProcedure();
                reflectionProcedure.setCreateTime(LocalDateTime.now());
                reflectionProcedure.setUpdateTime(LocalDateTime.now());
                reflectionProcedure.setQuizID(quizID);
                taskMapper.createReflectionProcedure(reflectionProcedure);
                taskMapper.createProcedureCategory(taskID, groupID, procedureOrderInTask, procedureType, reflectionProcedure.getId(), LocalDateTime.now(), LocalDateTime.now());
                break;
            }
            default:
                break;
        }
    }

    private Integer getProceduresType(String procedureOrderList, Integer procedureOrderInTask) {
        //去掉首尾的[]
        String trimProcedureOrderList = procedureOrderList.substring(1, procedureOrderList.length() - 1);
        //将每个字符串类型的数组存入字符数组中
        String[] procedureOrderArray = trimProcedureOrderList.split(",");
        //将字符数组转换为int类型数组
        int[] orderArray = Stream.of(procedureOrderArray)
                .mapToInt(Integer::parseInt)
                .toArray();
        return orderArray[procedureOrderInTask];
    }
}
