package com.itheima.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.itheima.config.EtherpadConfig;
import com.itheima.mapper.TaskMapper;
import com.itheima.mapper.UserMapper;
import com.itheima.pojo.FontNumStatisticsResult;
import com.itheima.pojo.Student;
import com.itheima.service.StatisticService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class StatisticServiceImpl implements StatisticService {
    @Autowired
    private EtherpadConfig etherpadConfig;
    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private UserMapper userMapper;
    @Override
    public FontNumStatisticsResult getStatisticByProcedureID(Integer procedureID, String sessionID) {
        String padID = taskMapper.getPadIDByProcedureID(procedureID);
        HashMap<String,Integer> result = new HashMap<>();
        HashMap<String,String> authorName = new HashMap<>();
        Map<String,Map<Integer,Map<String,String>>> resultMap =getPadAttribs(padID, sessionID);
        log.info("resultMap:{}",resultMap);

        String attribs = resultMap.keySet().iterator().next();
        Map<Integer, Map<String, String>> numToAttribMap = resultMap.get(attribs);

        System.out.println("attribs: " + attribs);
        System.out.println("numToAttribMap: " + numToAttribMap);

        // 解析 attribs 字符串
        Map<Integer, Integer> statistics = getStatistics(attribs);
        System.out.println("statistics: " + statistics);

        for (Map.Entry<Integer, Integer> entry : statistics.entrySet()) {
            Integer num = entry.getKey();
            Integer count = entry.getValue();
            Map<String, String> attribMap = numToAttribMap.get(num);
            String authorID = attribMap.get("author");
            Student student = userMapper.getStudentByAuthorID(authorID);
            authorName.put(authorID,student.getName());
            result.put(authorID,count);
        }
        FontNumStatisticsResult resultAndNameOfAuthorID = new FontNumStatisticsResult(result,authorName);
        return resultAndNameOfAuthorID;
    }

    private Map<Integer, Integer> getStatistics(String attribs) {
        Map<Integer, Integer> result = new HashMap<>();
        boolean inKey = false;
        StringBuilder currentKeyBuilder = new StringBuilder();
        StringBuilder currentValueBuilder = new StringBuilder();

        for (int i = 0; i < attribs.length(); i++) {
            char c = attribs.charAt(i);

            if ( inKey != true && c == '*') {
                inKey = true;
                i++; // Skip the next character which is the digit after '*'
                while (i < attribs.length() && Character.isDigit(attribs.charAt(i))) {
                    currentKeyBuilder.append(attribs.charAt(i));
                    i++;
                }
                i--; // Adjust for the extra increment in the loop
            } else if (c == '+' && inKey) {
                if (currentKeyBuilder.length() > 0) {
                    int key = Integer.parseInt(currentKeyBuilder.toString());
                    i++; // Skip the next character which is the digit after '+'
                    currentValueBuilder.setLength(0); // Clear the builder
                    while (i < attribs.length() && (Character.isDigit(attribs.charAt(i)) || Character.isLetter(attribs.charAt(i)))) {
                        currentValueBuilder.append(attribs.charAt(i));
                        i++;
                    }
                    i--; // Adjust for the extra increment in the loop

                    int valueToAdd = Integer.parseInt(currentValueBuilder.toString(), 36);
                    result.put(key, result.getOrDefault(key, 0) + valueToAdd);
                    inKey = false;
                    currentKeyBuilder.setLength(0); // Clear the key builder for the next key
                }
            }
        }

        return result;
    }
    private Map<String, Map<Integer, Map<String, String>>> getPadAttribs(String padID,String sessionID) {
        String urlString = etherpadConfig.getEtherpadUrl() + "/p/" + padID + "/export/etherpad" + "?apikey=" + etherpadConfig.getApikey();
        StringBuilder content = new StringBuilder();

        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");

            connection.setRequestProperty("Cookie", "sessionID=" + sessionID);

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line);
                    content.append(System.lineSeparator());
                }
            }
            String jsonString = content.toString();

            // 解析JSON字符串为JSONObject
            JSONObject jsonObject = JSONObject.parseObject(jsonString);
            String pad = "pad:" + padID;

            // 提取pad对象
            JSONObject padObject = jsonObject.getJSONObject(pad);
            System.out.println("padObject: " + padObject);

            // 提取atext对象
            JSONObject atextObject = padObject.getJSONObject("atext");
            // 提取attribs字段
            String attribs = atextObject.getString("attribs");
            System.out.println("attribs: " + attribs);

            // 提取pool对象
            JSONObject poolObject = padObject.getJSONObject("pool");
            // 提取numToAttrib对象
            JSONObject numToAttribObject = poolObject.getJSONObject("numToAttrib");

            // 遍历numToAttrib对象，将键值对放入Map中
            // 创建外层的 Map
            Map<Integer, Map<String, String>> numToAttribMap = new HashMap<>();

            // 遍历 numToAttribObject
            for (String key : numToAttribObject.keySet()) {
                // 将键转换为整数
                int num = Integer.parseInt(key);
                // 获取内层的 JSONArray
                JSONArray jsonArray = numToAttribObject.getJSONArray(key);
                // 创建内层的 Map
                Map<String, String> attributeMap = new HashMap<>();
                // 将 JSONArray 中的元素放入内层的 Map
                if (jsonArray.size() >= 2) {
                    String attrKey = jsonArray.getString(0);
                    String attrValue = jsonArray.getString(1);
                    attributeMap.put(attrKey, attrValue);
                }
                // 将内层的 Map 放入外层的 Map
                numToAttribMap.put(num, attributeMap);
            }

            // 输出结果
            for (Map.Entry<Integer, Map<String, String>> entry : numToAttribMap.entrySet()) {
                System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
            }

            Map<String, Map<Integer, Map<String, String>>> resultMap = new HashMap<>();
            resultMap.put(attribs, numToAttribMap);
            log.info("resultMap:{}", resultMap);
            return resultMap;

        } catch (IOException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }
}
