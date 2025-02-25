package com.itheima.server;


import com.alibaba.fastjson.JSONException;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.itheima.mapper.MessageMapper;
import com.itheima.pojo.GoNext;
import com.itheima.pojo.HeartMessage;
import com.itheima.pojo.Message;
import com.itheima.pojo.MindStormMessage;
import com.itheima.utils.JSONUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@ServerEndpoint(value = "/imserver/{chatroomId}")
@Component
@Slf4j
public class WebSocketServer implements InitializingBean {

    // 存储每个 Session 和其对应的 chatroomId
    public static final Map<Session, Integer> sessionToChatRoom = new ConcurrentHashMap<>();

    private static MessageMapper messageMapper;

    @Autowired
    public void setMessageMapper(MessageMapper messageMapper) {
        WebSocketServer.messageMapper = messageMapper;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        if (messageMapper == null) {
            System.out.println("MessageMapper is null");
        }
    }

    @OnOpen
    public void onOpen(Session session, @javax.websocket.server.PathParam("chatroomId") Integer chatroomId) {
        sessionToChatRoom.put(session, chatroomId);
        log.info(session.getId());
        log.info("New connection to chat room: " + chatroomId);
    }

    @OnMessage
    public void onMessage(String message, Session senderSession) {
        Integer chatroomId = sessionToChatRoom.get(senderSession);
        try {
            // 对这个message对象进行数据库操作，保存聊天记录
            log.info("收到的消息字符串：{}", message);
            Message messageObj = (Message) JSONUtils.jsonToBean(message, Message.class);
            if(messageObj.getSenderID()==null){
                throw new JSONException("不是此消息类型，可能是头脑风暴类型或投票类型");
            }
            log.info("转换后的消息对象：{}", messageObj);
            // 保存聊天记录
            if (messageMapper != null) {
                messageMapper.addMessage(messageObj);
            } else {
                log.error("messageMapper 为 null，无法保存聊天记录");
            }
        } catch (JSONException e1) {
            //如果不是普通类型的消息，那可能是投票类型的消息或者头脑风暴类型的消息
            log.error("JSON转换失败，无法转换成Message对象：{}", e1.getMessage(), e1);
            try {
                MindStormMessage mindStormObj = (MindStormMessage) JSONUtils.jsonToBean(message, MindStormMessage.class);
                if(mindStormObj.getProducerID()==null){
                    throw new JSONException("不是此消息类型,可能是投票类型");
                }
                log.info("转换后的MindStorm对象：{}", mindStormObj);
                // 处理MindStorm对象的逻辑
                if (messageMapper != null) {
                    mindStormObj.setCreateTime(LocalDateTime.now());
                    mindStormObj.setUpdateTime(LocalDateTime.now());
                    messageMapper.addMindStormMessage(mindStormObj);
                    // 创建一个 JsonObject
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("MindStormMessageID", mindStormObj.getId());
                    jsonObject.addProperty("MindStormMessage", message);

                    // 使用 Gson 将 JsonObject 转换为 JSON 字符串
                    Gson gson = new Gson();
                    message = gson.toJson(jsonObject);
                } else {
                    log.error("messageMapper 为 null，无法保存聊天记录");
                }
            } catch (JSONException e2) {
                //如果不是前两个类型的消息，那应该是goNext类型或者投票类型的消息
                log.error("JSON转换失败，无法转换成MindStorm对象：{}", e2.getMessage(), e2);
                try {
                    GoNext goNext = (GoNext) JSONUtils.jsonToBean(message, GoNext.class);
                    if(goNext.getTaskID()==null||goNext.getGroupID()==null||goNext.getUserID()==null){
                        log.error("不是gonext");
                        throw new JSONException("不是此消息类型,可能是投票类型");
                    }
                    log.info("转换后的MindStorm对象：{}", goNext);
                    // 处理MindStorm对象的逻辑
                    if (messageMapper != null) {
                        messageMapper.increaseGoNext(goNext);
                        messageMapper.addToTheyCantGoNext(goNext);
                    } else {
                        log.error("messageMapper 为 null，无法保存聊天记录");
                    }
                }catch (JSONException e3){
                     try {
                         HeartMessage heartMessage = (HeartMessage) JSONUtils.jsonToBean(message, HeartMessage.class);
                         if(heartMessage.getType()==null){
                             throw new JSONException("不是此消息类型,可能是投票类型");
                         }
                     }catch (JSONException e4){
                         // 使用 JsonParser 解析 JSON 字符串
                         JsonParser jsonParser = new JsonParser();
                         JsonElement jsonElement = jsonParser.parse(message);
                         // 获取键值对的值
                         String key1 = "mindID";
                         int mindID = jsonElement.getAsJsonObject().get(key1).getAsInt();
                         String key2 = "procedureID";
                         int procedureID = jsonElement.getAsJsonObject().get(key2).getAsInt();
                         String key3 = "userID";
                         int userID = jsonElement.getAsJsonObject().get(key3).getAsInt();
                         messageMapper.addScoreByMindID(mindID);
                         messageMapper.addCanThem(procedureID,userID);
                     }
                }
            }
        }
        finally {
            //转发消息
            if (chatroomId != null) {
                // 遍历所有 Session，找到属于同一 chatroomId 的 Session 并发送消息
                for (Map.Entry<Session, Integer> entry : sessionToChatRoom.entrySet()) {
                    log.info("发送消息到聊天室：" + entry.getValue());
                    log.info("发送消息到会话：" + entry.getKey().getId());
                    if (entry.getValue().equals(chatroomId)) {
                        log.info(String.valueOf(sessionToChatRoom.entrySet()));
                        try {
                            entry.getKey().getBasicRemote().sendText(message);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        log.info("会话不在chatroom当中");
                    }
                }
            }
        }
    }

    @OnClose
    public void onClose(Session session) {
        sessionToChatRoom.remove(session);
        System.out.println("Connection closed: " + session.getId());
    }
}
