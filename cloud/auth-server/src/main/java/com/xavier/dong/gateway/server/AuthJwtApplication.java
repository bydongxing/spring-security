package com.xavier.dong.gateway.server;


import com.alicp.jetcache.anno.config.EnableMethodCache;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 *
 * 授权服务启动
 *
 * @author xavierdong
 */
//@EnableDiscoveryClient
@SpringBootApplication
@EnableMethodCache(basePackages="com.xavier.dong.auth.server")
public class AuthJwtApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthJwtApplication.class, args);
    }
}