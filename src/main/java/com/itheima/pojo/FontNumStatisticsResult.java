package com.itheima.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FontNumStatisticsResult {
    private Map<String,Integer> numOfAuthorID;
    private Map<String,String> userNameOfAuthorID;
}
