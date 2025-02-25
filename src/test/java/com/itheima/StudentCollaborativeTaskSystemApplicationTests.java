package com.itheima;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.itheima.config.EtherpadConfig;
import com.itheima.utils.HttpUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class StudentCollaborativeTaskSystemApplicationTests {
    @Autowired
    EtherpadConfig etherpadConfig;

    @Test
    void contextLoads() {
    }


    @Test
    void testReadEtherpad() {
        HashMap<String,Integer> result = new HashMap<>();
        String padID = "RTkLr5KX0_Bc0UYzw8Ot";
        String urlString = etherpadConfig.getEtherpadUrl() + "/p/"+padID +"/export/etherpad"+ "?apikey=" + etherpadConfig.getApikey();
            StringBuilder content = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(new URL(urlString).openStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line);
                    content.append(System.lineSeparator());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            String jsonString = content.toString();

        // 解析JSON字符串为JSONObject
        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        String pad = "pad:"+padID;

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

        Map<String,Map<Integer,Map<String,String>>> resultMap = new HashMap<>();
        resultMap.put(attribs,numToAttribMap);
        System.out.println(resultMap);

        // 解析 attribs 字符串
        Map<Integer, Integer> statistics = getStatistics(attribs);
        System.out.println("statistics: " + statistics);

        for (Map.Entry<Integer, Integer> entry : statistics.entrySet()) {
            Integer num = entry.getKey();
            Integer count = entry.getValue();
            Map<String, String> attribMap = numToAttribMap.get(num);
            String authorID = attribMap.get("author");
            result.put(authorID,count);
        }
        System.out.println(result);
    }

    private Map<Integer, Integer> getStatistics(String attribs) {
        Map<Integer, Integer> result = new HashMap<>();
        boolean inKey = false;
        Integer keyNow;
        String sss = "+1*0|6+3y*0+f*0*1+3*0|3+30*0+f*0*1+3*0|1+2k*0+v*0*1+3*0|4+5t*0+17*0*1+b*0|3+3d*0+f*0*1+3*0|2+q*0+1|1+1" ;
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
    @Test
    void HttpTest() {


        HashMap<String, String> hm = new HashMap<>();
        hm.put("apikey", etherpadConfig.getApikey());
        System.out.println(etherpadConfig.getApikey());
        hm.put("name", "小明");
        hm.put("authorMapper", "1112");
        System.out.println(etherpadConfig.getEtherpadUrl() + etherpadConfig.getCreateAuthorIfNotExistsFor());
        String res = HttpUtils.get(etherpadConfig.getEtherpadUrl() + etherpadConfig.getCreateAuthorIfNotExistsFor(), hm);
        if (res == null) {
            throw new IllegalStateException("Http request returned null response");
        }
        JSONObject jsonObject = JSONObject.parseObject(res);
        JSONObject data = jsonObject.getJSONObject("data");
        String authorID = data.getString("authorID");
        System.out.println(authorID);
    }
}
