package com.hccmac.communityforumbackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 社区论坛后端启动类
 */
@SpringBootApplication
@MapperScan("com.hccmac.communityforumbackend.mapper")
public class CommunityforumBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommunityforumBackendApplication.class, args);
    }
}
