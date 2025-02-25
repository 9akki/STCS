package com.itheima.utils;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

public class renderResult {
    public static void renderResult(HttpServletResponse response, Boolean res){
        JSONObject json=new JSONObject();
        json.put("code",res?0:1);
        json.put("msg",res?"成功":"失败");
        renderResult(response,json);
    }

    public static void renderResult(HttpServletResponse response,String res){
        JSONObject json=new JSONObject();
        json.put("msg",res);
        renderResult(response,json);
    }

    public static void renderResult(HttpServletResponse response, Object obj){
        PrintWriter out = null;
        try {
            String jsonArray = JSONObject.toJSONString(obj);
            response.setContentType(
                    "text/html;charset=utf-8");
            out = response.getWriter();
            out.println(jsonArray);
            out.flush();
            out.close();
            return;
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            if (out != null) {
                out.flush();
                out.close();
            }
        }
    }
}

