package com.itheima.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskInfoOfGroup {
    private Integer id;
    private Integer taskID;
    private Integer groupID;
    private Integer chatRoomID;
    private Integer procedureNow;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private LocalDateTime endTime;
    private Integer readable;
    private Integer GoNext;
}
