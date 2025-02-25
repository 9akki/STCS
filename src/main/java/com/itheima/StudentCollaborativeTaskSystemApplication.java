package com.itheima;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@EnableWebSocket
@SpringBootApplication
public class StudentCollaborativeTaskSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudentCollaborativeTaskSystemApplication.class, args);
    }

}
