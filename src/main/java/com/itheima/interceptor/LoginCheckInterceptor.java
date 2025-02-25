package com.itheima.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.itheima.pojo.Result;
import com.itheima.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j

@Component
public class LoginCheckInterceptor implements HandlerInterceptor {
    @Override //目标资源方法运行前运行，返回true代表放行，反之拦截
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //1.获取请求url
        String url = request.getRequestURL().toString();
        log.info("请求的url：{}",url);

//        //2.判断请求url中是否包含login，如果包含，说明是登录操作，放行
//        if(url.contains("login")){
//            return true;
//        }

        //3.获取请求头中的令牌
        String jwt = request.getHeader("token");
        log.info("请求头中的令牌为：{}",jwt);
        //4.判断令牌是否存在
        if(!StringUtils.hasLength(jwt)){
            Result error = Result.error("NOT_LOGIN");
            String notLogin = JSONObject.toJSONString(error);
            response.getWriter().write(notLogin);
            return false;
        }

        //5.解析token，如果解析失败，返回错误结果（未登录）
        try {
            JwtUtils.parseJWT(jwt);
        }catch (Exception e){
            e.printStackTrace();
            log.info("解析令牌失败，返回未登录错误信息");
            Result error = Result.error("NOT_LOGIN");
            String notLogin = JSONObject.toJSONString(error);
            response.getWriter().write(notLogin);
            return false;
        }

        //6.放行
        return true;
    }

    @Override //目标资源方法运行后运行
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        System.out.println("postHandler ...");
    }

    @Override //视图渲染完成后运行，最后运行
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        System.out.println("afterCompletion ...");
    }
}
