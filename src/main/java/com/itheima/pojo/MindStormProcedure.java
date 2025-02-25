package com.itheima.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MindStormProcedure extends Procedure{
    private Integer id;
    private LocalDateTime endTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer beElectedClaimID;
}
