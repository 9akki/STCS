package com.itheima.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MindStormMessage {
    private Integer id;
    private String claim;
    private Integer producerID;
    private String ProducerName;
    private Integer score;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer procedureID;
}
