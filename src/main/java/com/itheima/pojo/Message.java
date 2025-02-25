package com.itheima.pojo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    @ExcelIgnore
    private Integer id;
    @ExcelProperty(value = "内容", index = 0)
    private String content;
    @ExcelProperty(value = "消息类型", index = 1)
    private String type;
    @ExcelProperty(value = "发送者", index = 2)
    private String sender;
    @ExcelIgnore
    private Integer senderID;
    @ExcelIgnore
    private String senderAvatar;
    @ExcelIgnore
    private String chatroomID;
    @ExcelProperty(value = "时间", index = 3)
    @JSONField(format = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timeStamp;
    private Integer toUser;
    @ExcelProperty(value = "接收者", index = 4)
    private String toUserName;
}
