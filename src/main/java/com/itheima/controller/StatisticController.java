package com.itheima.controller;

import com.itheima.pojo.FontNumStatisticsResult;
import com.itheima.pojo.Result;
import com.itheima.service.StatisticService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequestMapping("/statistics")
@RestController
@Slf4j
public class StatisticController {

    @Autowired
    private StatisticService statisticService;
    @GetMapping
    public Result getStatisticByProcedureID(Integer procedureID,String sessionID){
        FontNumStatisticsResult ultimateResult = statisticService.getStatisticByProcedureID(procedureID,sessionID);
        return Result.success(ultimateResult);
    }
}
