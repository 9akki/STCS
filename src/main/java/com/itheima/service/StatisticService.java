package com.itheima.service;

import com.itheima.pojo.FontNumStatisticsResult;

import java.util.HashMap;
import java.util.Map;

public interface StatisticService {
    FontNumStatisticsResult getStatisticByProcedureID(Integer procedureID, String sessionID);
}
