package com.xavier.dong.resource.server;


import com.alicp.jetcache.anno.config.EnableMethodCache;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 *
 * 资源服务启动
 *
 * @author xavierdong
 */
@EnableDiscoveryClient
@SpringBootApplication
@EnableMethodCache(basePackages="com.xavier.dong.resource.server")
public class ResourceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ResourceApplication.class, args);
    }
}