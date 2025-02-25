package com.itheima.utils;

import com.alibaba.fastjson.JSONObject;
import jodd.http.HttpRequest;
import jodd.http.HttpResponse;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HttpUtils {
    /**
     * @param url 调用接口
     * @param map 查询条件
     * @return String
     */
    public static String post(String url, Map<String, Object> map) {
//        String param = JSONObject.toJSONString(map);
        HttpResponse res = HttpRequest.post(url).connectionTimeout(90000).timeout(90000)
                .contentType("application/json", "UTF-8").bodyText(JSONObject.toJSONString(map))
                .send();
        res.charset("utf-8");
        return res.bodyText();
    }

    public static String post(String url, String jsonStr) {
        HttpResponse resp = HttpRequest.post(url).connectionTimeout(60000).timeout(60000)
                .contentType("application/json", StandardCharsets.UTF_8.toString()).body(jsonStr)
                .send();
        resp.charset(StandardCharsets.UTF_8.toString());
        return resp.bodyText();
    }

    /**
     * 发送Get请求
     *
     * @param url    : 请求的连接
     * @param params ： 请求参数，无参时传null
     * @return
     */
    public static String get(String url, Map<String, String> params) {
        HttpRequest request = HttpRequest.get(url);
        if (params != null) {
            request.query(params);
        }
        HttpResponse response = request.send();
        return response.bodyText();
    }
}
