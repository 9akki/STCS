package com.itheima.controller;

import com.itheima.pojo.Result;
import com.itheima.pojo.User;
import com.itheima.service.UserService;
import com.itheima.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
public class LoginController {
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    //用Map集合来获取请求体Json格式数据中的内容
    private Result login(@RequestBody Map<String, Object> map) {

        log.info("用户登录:{}", map.toString());

        //根据用户身份进行身份验证(教师/学生)
        User user;
        Integer isWho = (Integer) map.get("isWho");
        if (isWho==0)
            user = (User) userService.studentLogin((String)map.get("username"), (String)map.get("password"));
        else if (isWho==1)
            user = (User) userService.teacherLogin((String)map.get("username"), (String)map.get("password"));
        else if (isWho==2)
            user = (User) userService.adminLogin((String)map.get("username"), (String)map.get("password"));
        else
            return Result.error("未选择登录角色，请重试");

        try{
            if(user.getIsDelete()==1)
                return Result.error("该用户已注销，请联系管理员");
            log.info("用户信息:{}", user);
        }catch (Exception e){
            return Result.error("用户名或密码错误");
        }

        //登录成功，生成令牌，下发令牌
        if (user != null) {
            Map<String, Object> claims = new HashMap<>();
            claims.put("id", user.getId());
            claims.put("name", user.getName());
            claims.put("username", user.getUsername());
            claims.put("isWho",isWho);

            String jwt = JwtUtils.generateJwt(claims); //jwt包含了当前登录的员工信息
            return Result.success(jwt);
        }


        //登录失败，返回错误信息
        return Result.error("用户名或密码错误");
    }
}